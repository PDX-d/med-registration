package org.example.controller.doctor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.ScheduleDTO;
import org.example.pojo.dto.ScheduleStatusDTO;
import org.example.service.ScheduleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "排班管理")
@Slf4j
@RestController("doctorSchedule")
@RequestMapping("/doctor")
@Validated
public class ScheduleController {

	@Resource
	private ScheduleService scheduleService;

	@PostMapping("/schedule/set")
	@ApiOperation(value = "排班", notes = "医生排班")
	@RequirePermission("schedule:add")
	public Result schedule(@Valid @RequestBody ScheduleDTO scheduleDTO) {
		return scheduleService.add(scheduleDTO);
	}

	@GetMapping("/user/current")
	@ApiOperation(value = "获取当前用户信息", notes = "获取当前用户信息")
	@RequirePermission("schedule:add")
	public Result getCurrentUser() {
		return scheduleService.getCurrentUser();
	}

	@GetMapping("/schedule/my")
	@ApiOperation(value = "获取排班列表", notes = "获取排班列表")
	@RequirePermission("schedule:list")
	public Result list(@RequestParam Long page,
					   @RequestParam Long pageSize,
					   @RequestParam(required = false) String departmentId,
					   @RequestParam(required = false) String date,
					   @RequestParam(required = false) String doctorId
	) {
		return scheduleService.list(page, pageSize, departmentId, date, doctorId);
	}

	@PutMapping("/schedule/update")
	@ApiOperation(value = "修改排班", notes = "更新排班信息")
	@RequirePermission("schedule:update")
	public Result update(@Valid @RequestBody ScheduleStatusDTO scheduleDTO) {
		log.info("修改排班信息:{}", scheduleDTO);
		return scheduleService.updateStatus(scheduleDTO);
	}
}
