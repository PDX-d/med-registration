package org.example.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.common.result.Result;
import org.example.mapper.DoctorMapper;
import org.example.pojo.dto.ScheduleDTO;
import org.example.pojo.dto.ScheduleStatusDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Doctor;
import org.example.pojo.entity.Schedule;
import org.example.mapper.ScheduleMapper;
import org.example.pojo.vo.ScheduleVO;
import org.example.service.ScheduleService;
import org.example.common.utils.UserHolder;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.ERR_USER_NOT_LOGIN;
import static org.example.common.constants.OrderConstant.ERR_SCHEDULE_NOT_EXIST;
import static org.example.common.constants.RedisConstant.*;

@Service
@Slf4j
@CacheConfig(cacheNames = "scheduleCache") // 类级统一配置
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

	@Resource
	private ScheduleMapper scheduleMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private CopyMapper copyMapper;

	@Resource
	private DoctorMapper doctorMapper;

	//  管理端
	@Override
	public Result add(ScheduleDTO scheduleDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Schedule schedule = copyMapper.ScheduleDTOToSchedule(scheduleDTO);
		schedule.setRemainingCount(schedule.getMaxAppointments());
		schedule.setCreateTime(LocalDateTime.now());
		schedule.setUpdateTime(LocalDateTime.now());
		if (schedule.getScheduleDate() != null) {
			LocalDateTime finalReleaseTime = schedule.getScheduleDate().minusDays(3).atTime(8, 0, 0);
			log.info("排班开始时间:{}", finalReleaseTime);
			if (finalReleaseTime.isAfter(LocalDateTime.now())) {
				schedule.setReleaseTime(finalReleaseTime);
			}
			log.info("排班结束时间:{}", schedule.getReleaseTime());
		}

		log.info("排班信息:{}", schedule);
		try {
			scheduleMapper.insert(schedule);
		} catch (Exception e) {
			return Result.fail("时间冲突");
		}
		String cacheKey = DOCTOR_SCHEDULE_KEY + schedule.getDoctorId();
		stringRedisTemplate.delete(cacheKey);
		return Result.success();
	}

	@Override
	public Result list(Long page, Long pageSize, String departmentId, String doctorId, String date) {
		log.info("获取排班信息:{}", page);
		Page<Schedule> pageList = new Page<>(page, pageSize);
		LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
		if (departmentId != null) {
			wrapper.eq(Schedule::getDepartmentId, departmentId);
		}
		if (date != null) {
			wrapper.eq(Schedule::getScheduleDate, date);
		}
		if (doctorId != null) {
			wrapper.eq(Schedule::getDoctorId, doctorId);
		}
		wrapper.eq(Schedule::getIsDeleted, 0);
		wrapper.orderByDesc(Schedule::getScheduleDate);
		IPage<Schedule> resultPage = scheduleMapper.selectPage(pageList, wrapper);
		List<ScheduleVO> scheduleList = resultPage.getRecords().stream()
				.map(copyMapper::ScheduleToScheduleVO)
				.collect(Collectors.toList());
		return Result.success(scheduleList, resultPage.getTotal());
	}

	@Override
	public Result detail(Long id) {
		Schedule schedule = scheduleMapper.selectById(id);
		if (schedule == null) {
			return Result.fail(ERR_SCHEDULE_NOT_EXIST);
		}
		ScheduleVO scheduleVO = copyMapper.ScheduleToScheduleVO(schedule);
		return Result.success(scheduleVO);
	}

	@Override
	public Result update(ScheduleDTO scheduleDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Schedule schedule = copyMapper.ScheduleDTOToSchedule(scheduleDTO);
		schedule.setUpdateTime(LocalDateTime.now());
		scheduleMapper.update(schedule, new LambdaQueryWrapper<Schedule>().eq(Schedule::getId, schedule.getId()));
		clearDepartCache(schedule.getDoctorId());
		return Result.success();
	}

	@Override
	public Result delete(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		log.info("删除排班信息:{}", id);
		//获取医生id
		Long doctorId = scheduleMapper.selectById(id).getDoctorId();
		scheduleMapper.isDelete(id);
		clearDepartCache(doctorId);
		String cacheKey = APPOINT_ORDER_KEY + id;
		stringRedisTemplate.delete(cacheKey);
		return Result.success();
	}

	private void clearDepartCache(Long doctorId) {
		String cacheKey = DOCTOR_SCHEDULE_KEY + doctorId;
		stringRedisTemplate.delete(cacheKey);
	}

	//   用户端
	@Override
	public Result listById(Long doctorId) {
		String Key = DOCTOR_SCHEDULE_KEY + doctorId;
		String json = stringRedisTemplate.opsForValue().get(Key);
		if (StrUtil.isNotBlank(json)) {
			log.info(REDIS_YES);
			List<ScheduleVO> scheduleVOList = JSONUtil.toList(json, ScheduleVO.class);
			return Result.success(scheduleVOList);
		}
		LocalDate now = LocalDate.now();  // 必须 > 当前+30分钟才能约
		LambdaQueryWrapper<Schedule> Wrapper = new LambdaQueryWrapper<Schedule>();
		Wrapper.eq(Schedule::getDoctorId, doctorId)
				.eq(Schedule::getStatus, 1)
				.gt(Schedule::getScheduleDate, now)
				.eq(Schedule::getIsDeleted, 0)
		;    // > 30分钟后（不到30分不显示）
		List<Schedule> scheduleList = scheduleMapper.selectList(Wrapper);
		List<ScheduleVO> scheduleVOList = scheduleList.stream()
				.map(schedule -> copyMapper.ScheduleToScheduleVO(schedule))
				.collect(Collectors.toList());
		json = JSONUtil.toJsonStr(scheduleVOList);
		stringRedisTemplate.opsForValue().set(Key, json, HOME_TTL, TimeUnit.SECONDS);
		return Result.success(scheduleVOList);
	}

	@Override
	public Result getCurrentUser() {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Doctor doctor = doctorMapper.selectById(user.getId());
		Map<String, Object> map = new HashMap<>();
		map.put("doctorId", user.getId());
		map.put("departmentId", doctor.getDepartmentId());
		return Result.success(map);
	}

	@Override
	public Result updateStatus(ScheduleStatusDTO scheduleDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		try {
			scheduleMapper.updateStatus(scheduleDTO);
		} catch (Exception e) {
			return Result.fail("排班不存在");
		}
		clearDepartCache(scheduleDTO.getId());
		return Result.success();
	}
}
