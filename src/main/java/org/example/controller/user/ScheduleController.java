package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.service.ScheduleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;

@Api(tags = "用户排班")
@Slf4j
@RestController("userSchedule")
@RequestMapping("/schedule")
@Validated
public class ScheduleController {

	@Resource
	private ScheduleService scheduleService;

	@GetMapping("/doctor")
	@ApiOperation(value = "获取医生排班", notes = "根据医生ID获取排班信息列表")
	public Result scheduleList(@NotNull(message = "医生ID不能为空") @RequestParam Long doctorId) {
		log.info("获取排班信息:{}", doctorId);
		return scheduleService.listById(doctorId);
	}
}
