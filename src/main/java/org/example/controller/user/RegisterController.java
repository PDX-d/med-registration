package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;
import org.example.service.RegisterService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags = "用户注册")
@Slf4j
@RestController
@RequestMapping("/patient")
public class RegisterController {
	@Resource
	private RegisterService registerService;

	//注册
	@PostMapping("/register")
	@ApiOperation(value = "用户注册", notes = "新用户注册账号")
	public Result register(@RequestBody SysUser user) {
		return registerService.register(user);
	}
}
