package org.example.controller.admin;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.UpdateStatus;
import org.example.service.InfoService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(tags = "用户信息管理")
@Slf4j
@RestController
@RequestMapping("/admin")
public class InfoController {

	@Resource
	private InfoService infoService;

	@Resource
	private UserMapper userMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@GetMapping("/patient/list")
	@ApiOperation(value = "获取用户信息", notes = "获取用户信息")
	@RequirePermission("admin:user:list")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String keyword
	) {
		return infoService.list(page, pageSize, keyword);
	}
	@PutMapping("/patient/updateStatus")
	@ApiOperation(value = "更新用户状态", notes = "更新用户状态")
	@RequirePermission("admin:user:updateStatus")
	public Result updateStatus(@Valid @RequestBody UpdateStatus updateStatus) {
		return infoService.updateStatus(updateStatus);
	}
}
