package org.example.controller.doctor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.AppointCancelDTO;
import org.example.service.AppointService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController("doctorAppoint")
@RequestMapping("/doctor/appointment")
@Api(tags = "医生预约")
@Validated
public class AppointController {
	@Resource
	private AppointService appointService;

	@GetMapping("/my")
	@RequirePermission("doctor:appoint:list")
	@ApiOperation(value = "获取预约列表", notes = "获取预约列表")
	public Result list(@RequestParam Long page,
					   @RequestParam Long pageSize,
					   @RequestParam(required = false) String status,
					   @RequestParam(required = false) String Time,
					   @RequestParam(required = false) String keyword
	) {
		return appointService.DoctorList(page, pageSize, status, Time, keyword);
	}

	@PutMapping("/confirm/{id}")
	@RequirePermission("doctor:appoint:confirm")
	@ApiOperation(value = "确认预约", notes = "确认预约")
	public Result confirm(@PathVariable @NotNull(message = "预约ID不能为空") Long id) {
		return appointService.confirm(id);
	}

	@PutMapping("/cancel")
	@RequirePermission("doctor:appoint:cancel")
	@ApiOperation(value = "取消预约", notes = "取消预约")
	public Result cancel(@Valid @RequestBody AppointCancelDTO cancelDTO) {
		return appointService.doctorCancel(cancelDTO);
	}
}
