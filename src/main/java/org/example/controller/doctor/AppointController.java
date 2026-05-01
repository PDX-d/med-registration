package org.example.controller.doctor;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.service.AppointService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController("doctorAppoint")
@RequestMapping("/doctor/appointment")
@Api(tags = "医生预约")
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

}
