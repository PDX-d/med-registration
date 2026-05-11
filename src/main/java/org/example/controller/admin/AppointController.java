package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.service.AppointService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;


@Slf4j
@RestController("adminAppoint")
@RequestMapping("/admin/appointment")
@Api(tags = "预约管理")
@Validated
public class AppointController {

	@Resource
	private AppointService appointService;

	@GetMapping("/list")
	@ApiOperation(value = "管理端预约列表", notes = "全部的预约列表")
	@RequirePermission("appoint:list")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String status,
					   @RequestParam(required = false) String time,
					   @RequestParam(required = false) String keyword,
					   @RequestParam(required = false) String doctorKeyword
	) {
		return appointService.adminList(page, pageSize, status, time, keyword, doctorKeyword);
	}
}
