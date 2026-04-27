package org.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.AppointOrderDTO;
import org.example.pojo.dto.OrderDelayMessageDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.AppointOrder;
import org.example.pojo.entity.Schedule;
import org.example.mapper.AppointMapper;
import org.example.mapper.ScheduleMapper;
import org.example.common.utils.RedisIdWorker;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static java.lang.Thread.sleep;
import static org.example.common.constants.OrderConstant.*;
import static org.example.common.constants.RabbitMQConstant.*;
import static org.example.common.constants.RedisConstant.APPOINT_ORDER_STATUS_KEY;

@Component
@Slf4j
public class AppointOrderConsumer {

	@Resource
	private ScheduleMapper scheduleMapper;

	@Resource
	private RedisIdWorker redisIdWorker;

	@Resource
	private AppointMapper appointMapper;
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@RabbitListener(queues = APPOINT_ORDER_QUEUE)
	public void handle(AppointOrderDTO appointOrderDTO) {
		log.info("开始处理订单：{}", appointOrderDTO.getOrderId());
		try {
			// 2. 排班校验
			Schedule schedule = scheduleMapper.selectById(appointOrderDTO.getScheduleId());
			if (schedule == null) {
				log.error(ERR_SCHEDULE_NOT_EXIST);
				return;
			}
			// 3. 扣减库存
			int isSuccess = scheduleMapper.updateCount(schedule);
			if (isSuccess == 0) {
				// 库存不足
				log.error(ERR_STOCK_NOT_ENOUGH);
				return;
			}
			// 4. 创建订单
			UserDTO userDTO = appointOrderDTO.getUserDTO();
			AppointOrder appointOrder = new AppointOrder();
			// 订单信息
			appointOrder.setOrderId(appointOrderDTO.getOrderId());

			// 用户信息
			appointOrder.setPatientId(userDTO.getId());
			appointOrder.setPatientName(userDTO.getName());
			appointOrder.setPhone(userDTO.getPhone());

			// 医院/医生/排班
			appointOrder.setDepartId(appointOrderDTO.getDepartId());
			appointOrder.setDoctorId(appointOrderDTO.getDoctorId());
			appointOrder.setScheduleId(appointOrderDTO.getScheduleId());
			appointOrder.setScheduleDate(schedule.getScheduleDate());
			appointOrder.setTimeSlot(schedule.getTimeSlot());

			String key = APPOINT_ORDER_STATUS_KEY + appointOrderDTO.getOrderId();
			String status = stringRedisTemplate.opsForValue().get(key);
			// 状态
			appointOrder.setPayStatus(Integer.valueOf(status == null ? "0" : status));
			// 支付截止时间
			appointOrder.setPayDeadline(LocalDateTime.now().plusMinutes(15));
			appointOrder.setCreateTime(LocalDateTime.now());
			appointOrder.setUpdateTime(LocalDateTime.now());
			appointOrder.setFee(BigDecimal.valueOf(10));

			// 插入数据库
			appointMapper.insert(appointOrder);
			log.info("生成订单成功：{}", appointOrderDTO.getOrderId());
			if (status != null && status.equals("1")) {
				log.info("订单已支付,不需要进入延迟队列");
				stringRedisTemplate.delete(key);
				return;
			}

			// 4. 创建支付信息 进入延迟队列 等待支付
			/*
			项目难点
			针对高并发抢号场景下数据库压力过大、异步订单创建与前端支付时序不一致、
			订单支付时序不一致、支付超时未支付导致订单状态错乱、支付成功后订单状态未更新、
			RabbitMQ 延迟消息无法撤回导致订单状态错乱等问题，
			设计并实现了一套基于 Redis + 异步 MQ + 梯度延迟队列 的高可用支付校验方案：
			通过 Redis 原子扣减号源并存储订单支付状态，同步返回订单 ID 保证前端可立即支付，
			异步落库 MySQL 降低数据库压力；同时采用 10s/30s/1min/5min/10min 梯度延迟检查，
			以 Redis 状态为唯一权威依据，动态终止后续检查流程，解决了延迟消息不可撤回、状态不一致的痛点；
			超时未支付则自动回滚号源并更新数据库订单状态，既保证了高并发下的系统稳定性，
			又实现了号源快速复用与数据最终一致性。

			我在做抢号支付模块时，遇到了一个异步时序导致的订单查询问题：
			我的抢号接口是用 Redis+Lua 扣减号源，成功后直接返回预约成功，
			订单创建是交给 RabbitMQ 异步创建的。
			这里就出现了一个 BUG：前端收到预约成功，立刻跳转到支付页，
			但 MQ 消息还没消费、数据库还没生成订单，导致支付接口查不到订单 ID，用户无法支付。
			解决方法就是:
			预约成功后直接生成订单id返回给前端，前端收到订单ID后，再调用订单查询接口，
			但是这样就出现了另外一个问题就是前端拿到id后去调用支付接口,但是订单未生成
			解决方法就是:
			将订单id做key 支付状态做value存储在redis中 这样就算没有创建订单 也能查询到订单状态,完成支付
			后面创建订单后再去同步订单状态

			就是订单预约成功后 然后立即支付了然后redis挂了 创建订单就拿不到支付状态了
			解决方法就是:
			支付时我会同步写入 MySQL 支付记录表做兜底，MQ 创建订单时查询这张表，而不是只依赖 Redis。
			就算 Redis 挂了，也能拿到真实支付状态，保证订单状态绝对一致，不会出错。

			 */

			OrderDelayMessageDTO message = new OrderDelayMessageDTO();
			message.setOrderId(appointOrderDTO.getOrderId());
			message.setUserId(userDTO.getId());
			// 第一次延迟时间
			message.setRetryCount(0);
			// 延迟时间级别
			rabbitTemplate.convertAndSend(
					ORDER_DELAY_EXCHANGE,
					ORDER_DELAY_ROUTING_KEY,
					message,
					msg -> {
						msg.getMessageProperties().setDelay((int) DELAY_TIME_LEVELS[0]);
						return msg;
					});
		} catch (Exception e) {
			// 消费者统一捕获异常，避免MQ无限重试
			log.error("订单处理异常！消息：{}", appointOrderDTO, e);
		}
	}
}
