package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.SysLog;
import org.example.common.mapstruct.CopyMapper;
import org.example.mapper.*;
import org.example.pojo.entity.*;
import org.example.pojo.dto.AppointOrderDTO;
import org.example.pojo.dto.PayDTO;
import org.example.common.result.Result;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.vo.AppointVO;
import org.example.service.AppointService;
import org.example.common.utils.RedisIdWorker;
import org.example.common.utils.UserHolder;
import org.example.pojo.vo.AppointOrderVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.common.constants.OrderConstant.*;
import static org.example.common.constants.RabbitMQConstant.*;
import static org.example.common.constants.RedisConstant.*;
import static org.example.common.constants.MessageConstant.ERR_USER_NOT_LOGIN;

@Service
@Slf4j
public class AppointServiceImpl implements AppointService {

	@Resource
	private AppointMapper appointMapper;

	@Resource
	private DoctorMapper doctorMapper;

	@Resource
	private RedisIdWorker redisIdWorker;


	@Resource
	private StringRedisTemplate stringRedisTemplate;

	private static final DefaultRedisScript<Long> APPOINT_SCRIPT;
	private static final DefaultRedisScript<Long> CANCEL_SCRIPT;

	//预约脚本
	static {
		APPOINT_SCRIPT = new DefaultRedisScript<>();
		APPOINT_SCRIPT.setLocation(new ClassPathResource("Lua/appointOrder.lua"));
		APPOINT_SCRIPT.setResultType(Long.class);
	}

	//取消预约脚本
	static {
		CANCEL_SCRIPT = new DefaultRedisScript<>();
		CANCEL_SCRIPT.setLocation(new ClassPathResource("Lua/appointCancel.lua"));
		CANCEL_SCRIPT.setResultType(Long.class);
	}

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private ScheduleMapper scheduleMapper;

	@Resource
	private RedissonClient redissonClient;
	@Resource
	private CopyMapper copyMapper;
	@Resource
	private DepartMapper departMapper;

	@Resource
	private UserMapper userMapper;

	//预约
	@Override
	@SysLog("用户预约订单")
	public Result makeAppointment(AppointOrderDTO appointOrderDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		//redis+lua 判断和扣减
		Long result = stringRedisTemplate.execute(
				APPOINT_SCRIPT,
				Collections.emptyList(),
				appointOrderDTO.getScheduleId().toString(),
				user.getId().toString());
		int r = 0;
		if (result != null) {
			r = result.intValue();
		}
		if (r != 0) {
			log.info("预约失败 {}", r == 1 ? ERR_STOCK_NOT_ENOUGH : ERR_USER_EXIST);
			return Result.fail(r == 1 ? ERR_STOCK_NOT_ENOUGH : ERR_USER_EXIST);
		}
		//预约成功 生成订单id
		Long orderId = redisIdWorker.nextId(APPOINT_ORDER);
		Map<String, Object> orderMap = new HashMap<>();
		orderMap.put("orderId", orderId);
		appointOrderDTO.setOrderId(orderId);
		appointOrderDTO.setUserDTO(user);

		//创建redis支付状态
		String key = APPOINT_ORDER_STATUS_KEY + orderId;
		stringRedisTemplate.opsForValue().set(key,
				String.valueOf(PAY_STATUS_UNPAID),
				20,
				TimeUnit.MINUTES
		);
		log.info("进入队列");
		try {
			rabbitTemplate.convertAndSend(
					APPOINT_ORDER_EXCHANGE,
					APPOINT_ORDER_ROUTING_KEY,
					appointOrderDTO
			);
		} catch (AmqpException e) {
			log.error("消息发送失败");
			stringRedisTemplate.execute(
					CANCEL_SCRIPT,
					Collections.emptyList(),
					appointOrderDTO.getScheduleId().toString(),
					user.getId().toString()
			);
			throw new RuntimeException(e);
		}

		return Result.success(orderMap);
	}


	//前端redis实时查看预约数
	@Override
	public Result AppointCount(Long scheduleId) {
		String key = APPOINT_ORDER_KEY + scheduleId;
		String count = stringRedisTemplate.opsForValue().get(key);
		if (StrUtil.isNotBlank(count)) {
			return Result.success(count);
		}
		Schedule schedule = scheduleMapper.selectById(scheduleId);
		if (schedule == null) {
			return Result.fail("排班不存在");
		}
		stringRedisTemplate.opsForValue().set(key, String.valueOf(schedule.getRemainingCount()));
		return Result.success(String.valueOf(schedule.getRemainingCount()));
	}

	//预约列表
	@Override
	public Result list() {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		//查询当前用户订单
		User user = userMapper.selectById(userDTO.getId());
		LambdaQueryWrapper<AppointOrder> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(AppointOrder::getPatientId, user.getId());
		List<AppointOrder> appointOrders = appointMapper.selectList(wrapper);
		if (appointOrders.isEmpty()) {
			return Result.success(Collections.emptyList());
		}

		// 批量查询医生：收集所有不重复的 doctorId，一次查出
		List<Long> doctorIds = appointOrders
				.stream()
				.map(AppointOrder::getDoctorId)
				.distinct()
				.collect(Collectors.toList());
		Map<Long, Doctor> doctorMap = doctorMapper.selectBatchIds(doctorIds)
				.stream()
				.collect(Collectors.toMap(Doctor::getId, doctor -> doctor));
// 批量查询科室：收集所有不重复的 departmentId，一次查出
		List<Long> departIds = appointOrders
				.stream()
				.map(AppointOrder::getDepartId)
				.distinct()
				.collect(Collectors.toList());
		Map<Long, Depart> departMap = departIds.isEmpty()
				? Collections.emptyMap()
				: departMapper.selectBatchIds(departIds).stream()
				.collect(Collectors.toMap(Depart::getId, d -> d));
		// 组装 VO
		List<AppointOrderVO> appointOrderVOS = new ArrayList<>();
		for (AppointOrder appointOrder : appointOrders) {
			AppointOrderVO appointOrderVO = copyMapper.AppointToAppointVO(appointOrder);
			Doctor doctor = doctorMap.get(appointOrder.getDoctorId());
			if (doctor != null) {
				appointOrderVO.setDoctorName(doctor.getRealName());
				appointOrderVO.setTitle(doctor.getTitle());
				Depart depart = departMap.get(doctor.getDepartmentId());
				if (depart != null) {
					appointOrderVO.setDepartmentName(depart.getName());
				}
			}
			appointOrderVO.setPatientName(user.getRealName());
			appointOrderVO.setPatientPhone(user.getPhone());
			appointOrderVOS.add(appointOrderVO);
		}
		return Result.success(appointOrderVOS);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@SysLog("用户支付订单")
	public Result pay(PayDTO payDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			log.info("用户未登录");
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		String key = APPOINT_ORDER_STATUS_KEY + payDTO.getOrderId();
		Long userId = user.getId();
		RLock lock = redissonClient.getLock("appointOrder:" + payDTO.getOrderId());
		boolean isLock = lock.tryLock();
		if (!isLock) {
			return Result.fail("操作过于频繁");
		}
		log.debug("获取锁成功");
		try {
			//订单查询一开始就有订单查询了
			AppointOrder order = appointMapper.selectByOrderId(payDTO.getOrderId());
			if (order == null) {
				return Result.fail("订单不存在");
			}
			if (!order.getPatientId().equals(userId)) {
				log.warn("用户{}尝试支付他人订单{}, 订单所属用户{}",
						userId, payDTO.getOrderId(),
						order.getPatientId());
				return Result.fail("无权支付他人订单");
			}

			String status = stringRedisTemplate.opsForValue().get(key);
			if (status == null || !status.equals(String.valueOf(PAY_STATUS_UNPAID))) {
				//订单不存在或者已经支付
				return Result.fail("支付失败");
			}

			stringRedisTemplate.opsForValue().set(key, String.valueOf(PAY_STATUS_PAID));
			log.info("支付成功");
			//支付记录表兜底
			PayRecord payRecord = new PayRecord();
			payRecord.setOrderId(payDTO.getOrderId());
			payRecord.setPayTime(LocalDateTime.now());
			payRecord.setPayStatus(String.valueOf(PAY_STATUS_PAID));
			//添加支付记录
			appointMapper.addPayRecord(payRecord);
			Map<String, Object> map = new HashMap<>();
			map.put("orderId", payDTO.getOrderId());
			map.put("status", PAY_STATUS_PAID);
			map.put("payTime", LocalDateTime.now());
			appointMapper.updateStatus(map);
		} catch (Exception e) {
			log.error("支付失败");
			// 异常时手动回滚Redis状态
			stringRedisTemplate.opsForValue().set(key, String.valueOf(PAY_STATUS_UNPAID));
			throw new RuntimeException("支付失败", e);  // + 加上这一行
		} finally {
			lock.unlock();
		}
		return Result.success();
		/**
		 第一个是越权漏洞:
		 取消和支付接口一开始都没有校验订单归属，就是说只要传个orderId就能取消任何人的订单。
		 这个问题我是在做接口测试的时候发现的。
		 解决方式:就是在Service层从UserHolder拿当前登录的userId，查询订单之后和订单里的患者ID比对，如果不一致直接返回无权操作。
		 第二个是并发安全问题:支付状态的检查和设置不是原子操作，高并发下会出现重复支付。
		 一开始用jmeter压测，100并发大概有3%的重复支付情况。
		 解决方式是第一:按orderId加Redisson分布式锁；
		 第二，异常的时候手动回滚Redis的支付状态；
		 第三，保证所有数据库操作在同一个事务里。
		 第三个是用户身份篡改风险:最开始直接用前端传过来的userId，后来意识到这是个大问题
		 用户完全可以在浏览器里修改参数去支付别人的订单。
		 所以后来改成了一律从服务端的UserHolder里取userId，
		 前端传的用户标识不相信。
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@SysLog("用户取消订单")
	public Result cancel(Long orderId, Long userId) {
		String idempotentKey = "appoint:idempotent:cancel:" + orderId;
		// 原子抢锁：30分钟过期，同一订单只能执行1次
		Boolean lock = stringRedisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", 15, TimeUnit.MINUTES);
		if (Boolean.FALSE.equals(lock)) {
			log.info("订单{}已取消，幂等拦截，重复执行", orderId);
			return Result.fail("订单已取消，请勿重复操作");
		}
		String key = APPOINT_ORDER_STATUS_KEY + orderId;
		try {
			//redis获取订单状态
			AppointOrder appointOrder = appointMapper.selectByOrderId(orderId);
			String status = String.valueOf(appointOrder.getOrderStatus());
			if (status.equals(String.valueOf(PAY_STATUS_FAILED))) {
				//订单不存在或者还没支付
				return Result.fail("取消失败");
			}
			//订单取消
			Result extracted = extracted(orderId, userId);
			if (extracted.getCode() != 200) {
				return extracted;
			}
		} catch (Exception e) {
			stringRedisTemplate.delete(idempotentKey);
		}
		stringRedisTemplate.delete(key);
		return Result.success();
	}

	@Override
	public Result DoctorList(Long page, Long pageSize, String status, String time, String keyword) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Page<AppointOrder> pageParam = new Page<>(page, pageSize);
		IPage<AppointVO> pageModel = appointMapper
				.selectDoctorListPage(pageParam, status, time, keyword, user.getId());

		return Result.success(pageModel.getRecords(), pageModel.getTotal());
	}


	public Result extracted(Long orderId, Long userId) {
		AppointOrder appointCancel = appointMapper.selectByOrderId(orderId);
		if (appointCancel == null) {
			log.info("排班不存在");
			return Result.fail("排班不存在");
		}
		//权限校验 验证该订单是不是该用户的
		if (!appointCancel.getPatientId().equals(userId)) {
			log.info("用户无权限取消");
			return Result.fail("用户无权限取消");
		}
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		map.put("status", ORDER_STATUS_CANCEL);
		map.put("cancelTime", LocalDateTime.now());
		//更新订单状态
		appointMapper.updateStatus(map);
		// 1. 数据库恢复号源
		scheduleMapper.updateCountUp(appointCancel.getScheduleId());
		//Redis操作状态
		Long result = stringRedisTemplate.execute(
				CANCEL_SCRIPT,
				Collections.emptyList(),
				appointCancel.getScheduleId().toString(),
				appointCancel.getPatientId().toString()
		);
		int r;
		if (result != null) {
			r = result.intValue();
			if (r != 0) {
				log.info("取消失败");
				return Result.fail("取消失败");
			}
		}
		log.info("订单取消成功 orderId={}", orderId);
		return Result.success();
	}
}
