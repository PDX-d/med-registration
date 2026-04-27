package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@Api(tags = "管理员管理")
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Resource
	private AdminService adminService;

	@GetMapping("/list")
	@ApiOperation(value = "获取管理员列表", notes = "分页获取管理员列表")
	public Result list(@RequestParam Long page,
					   @RequestParam Long pageSize,
					   @RequestParam(required = false) String keyword
	) {
		return adminService.list(page, pageSize, keyword);
	}
}
