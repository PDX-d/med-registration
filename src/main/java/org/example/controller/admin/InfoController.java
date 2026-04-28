package org.example.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.Password;
import org.example.common.result.Result;
import org.example.service.InfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(tags = "信息管理")
@Slf4j
@RestController
@RequestMapping
public class InfoController {

	@Resource
	private InfoService infoService;

	@GetMapping("/info")
	@ApiOperation(value = "获取用户信息", notes = "获取当前登录用户的详细信息")
	public Result info(){
		log.info("获取信息");
		return infoService.info();
	}

	@PutMapping("/password")
	@ApiOperation(value = "修改密码", notes = "更新用户登录密码")
	public Result updatePassword(@RequestBody Password  password){
		log.info("修改用户名:{}", password);
		return infoService.updatePassword(password);
	}

}
