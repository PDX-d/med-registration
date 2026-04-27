package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.ScheduleDTO;
import org.example.pojo.entity.Schedule;
import org.example.service.ScheduleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(tags = "排班管理")
@Slf4j
@RestController
@RequestMapping("/schedule")
@Validated
public class ScheduleController {

	@Resource
	private ScheduleService scheduleService;

	@PostMapping("/add")
	@RequirePermission("schedule:add")
	@ApiOperation(value = "添加排班", notes = "新增医生排班信息")
	public Result add(@Valid @RequestBody ScheduleDTO scheduleDTO) {
		return scheduleService.add(scheduleDTO);
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取排班列表", notes = "分页获取排班列表，支持科室和日期筛选")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String departmentId,
					   @RequestParam(required = false) String date
	) {
		return scheduleService.list(page, pageSize, departmentId, date);
	}

	@GetMapping("/detail/{id}")
	@ApiOperation(value = "获取排班详情", notes = "根据ID获取排班详细信息")
	public Result detail(@NotNull(message = "ID不能为空") @PathVariable Long id) {
		log.info("获取排班信息: {}", id);
		return scheduleService.detail(id);
	}

	@PutMapping("/update")
	@RequirePermission("schedule:update")
	@ApiOperation(value = "修改排班", notes = "更新排班信息")
	public Result update(@Valid @RequestBody ScheduleDTO scheduleDTO) {
		log.info("修改排班信息:{}", scheduleDTO);
		return scheduleService.update(scheduleDTO);
	}

	@DeleteMapping("/delete/{id}")
	@RequirePermission("schedule:delete")
	@ApiOperation(value = "删除排班", notes = "根据ID删除排班信息")
	public Result delete(@NotNull(message = "ID不能为空")@PathVariable Long id) {
		log.info("删除排班信息:{}", id);
		return scheduleService.delete(id);
	}
}
