package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.Password;
import org.example.common.result.Result;
import org.example.pojo.dto.UserDTO;
import org.example.mapper.InofMapper;
import org.example.service.InfoService;
import org.example.common.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import static org.example.common.constants.MessageConstant.ERR_USER_NOT_LOGIN;

@Service
@Slf4j
public class InfoServiceImpl implements InfoService {
@Resource
private InofMapper inofMapper;

	@Override
	public Result info() {
		UserDTO user = UserHolder.getUser();
		log.info("获取信息:{}", user);
		return Result.ok(user);
	}

	@Override
	public Result updateUsername(String email, String realName) {
		Long userId = UserHolder.getUser().getId();
		if(userId==null){
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		inofMapper.updateUsername(email, realName,userId);
		return Result.ok();
	}

	@Override
	public Result updatePassword(Password password) {
		Long userId = UserHolder.getUser().getId();
		if(userId==null){
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		//1判断密码
		if(!password.getNewPassword().equals(password.getConfirmPassword())){
			return Result.fail("两次密码不一致");
		}
		Integer user =inofMapper.updatePassword(userId,password.getNewPassword(),password.getOldPassword());
		if(user == null){
			return Result.fail("旧密码错误");
		}
		log.info("修改成功");
		return Result.ok();
	}
}
