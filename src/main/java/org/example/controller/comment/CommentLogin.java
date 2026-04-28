package org.example.controller.comment;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;
import org.example.service.LoginService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/comment")
@Slf4j
public class CommentLogin {

	@Resource
	private LoginService loginService;

	@PostMapping("/sendCode")
	@ApiOperation(value = "发送验证码")
	public Result sendCode(@RequestParam String phone) {
		return loginService.sendCode(phone);
	}

	@PostMapping("/login")
	@ApiOperation(value = "登录")
	public Result login(@Valid @RequestBody SysUser user, HttpServletRequest  request) {
		return loginService.Login(user,request);
	}

	@PostMapping("/logout")
	@ApiOperation(value = "退出登录")
	public Result logout() {
		return loginService.logout();
	}
}
