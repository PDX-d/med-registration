package org.example.controller.user;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;
import org.example.service.LoginService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
@Api(tags = "用户登录")
@Slf4j
@RestController
@RequestMapping("/patient")
public class LoginController {
	@Resource
	private LoginService loginService;

	@PostMapping("/login")
	public Result login(@RequestBody SysUser user) {
		return loginService.Login(user);
	}
}
