package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.PasswordDTO;
import org.example.pojo.dto.PhoneUpdateDTO;
import org.example.pojo.dto.UserInfoDTO;
import org.example.service.InfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@Api(tags = "用户信息")
@RestController("infoUser")
@RequestMapping("/user")
public class InfoController {

	@Resource
	private InfoService infoService;

	@PutMapping("/update")
	@ApiOperation("更新用户信息")
	@RequirePermission("user:update")
	public Result update(@Valid @RequestBody UserInfoDTO userInfoDTO) {
		return infoService.updateInfo(userInfoDTO);
	}

	@GetMapping("/info")
	@ApiOperation("获取用户信息")
	@RequirePermission("user:detail")
	public Result detail() {
		return infoService.info();
	}

	@PutMapping("/change-phone")
	@ApiOperation("修改手机号")
	@RequirePermission("user:updatePhone")
	public Result changePhone(@Valid @RequestBody PhoneUpdateDTO phoneUpdateDTO) {
		return infoService.updatePhone(phoneUpdateDTO);
	}

	@PutMapping("/change-password")
	@ApiOperation("修改密码")
	@RequirePermission("user:updatePassword")
	public Result changePassword(@Valid @RequestBody PasswordDTO PasswordDTO) {
		return infoService.updatePassword(PasswordDTO);
	}
}
