package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.common.utils.DesensitizeUtil;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.Password;
import org.example.common.result.Result;
import org.example.pojo.dto.PhoneUpdateDTO;
import org.example.pojo.dto.UserDTO;
import org.example.mapper.InfoMapper;
import org.example.pojo.dto.UserInfoDTO;
import org.example.pojo.entity.User;
import org.example.pojo.vo.UserVO;
import org.example.service.InfoService;
import org.example.common.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import static org.example.common.constants.MessageConstant.*;

@Service
@Slf4j
public class InfoServiceImpl implements InfoService {
	@Resource
	private InfoMapper infoMapper;

	@Resource
	private UserMapper userMapper;
	@Resource
	private CopyMapper copyMapper;
	@Resource
	private LoginServiceImpl loginService;


	@Override
	public Result info() {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		User user = userMapper.selectById(userDTO.getId());
		UserVO userVO = copyMapper.UserToUserVO(user);
		//脱敏
		userVO.setPhone(DesensitizeUtil.phone(user.getPhone()));
		userVO.setIdCard(DesensitizeUtil.idCard(user.getIdCard()));
		return Result.ok(userVO);
	}

	@Override
	public Result updateInfo(UserInfoDTO userInfoDTO) {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		User user = copyMapper.UserInfoDTOToUser(userInfoDTO);
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(User::getId, userDTO.getId());
		int update = userMapper.update(user, queryWrapper);
		if (update <= 0) {
			return Result.fail(USER_INFO_MODIFY_ERROR);
		}
		return Result.ok();
	}

	@Override
	public Result updatePhone(PhoneUpdateDTO phoneUpdateDTO) {
		Boolean isSuccess = loginService.verifyLoginCode(phoneUpdateDTO.getNewPhone(), phoneUpdateDTO.getCode());
		if (!isSuccess) {
			return Result.fail(LOGIN_CODE_ERROR);
		}
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		User user = userMapper.selectById(userDTO.getId());
		if (!user.getPhone().equals(phoneUpdateDTO.getOldPhone())) {
			return Result.fail(OLD_PHONE_ERROR);
		}
		try {
			userMapper.updateUserPhone(phoneUpdateDTO.getNewPhone(), userDTO.getId());
			userMapper.updateSysUserPhone(phoneUpdateDTO.getNewPhone(), userDTO.getId());
		} catch (Exception e) {
			log.error("数据库更新失败");
			return Result.fail(PHONE_UPDATE_ERROR);
		}
		return Result.ok();
	}


	@Override
	public Result updatePassword(Password password) {
		Long userId = UserHolder.getUser().getId();
		if (userId == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		//1判断密码
		if (!password.getNewPassword().equals(password.getConfirmPassword())) {
			return Result.fail("两次密码不一致");
		}
		Integer user = infoMapper.updatePassword(userId, password.getNewPassword(), password.getOldPassword());
		if (user == null) {
			return Result.fail("旧密码错误");
		}
		log.info("修改成功");
		return Result.ok();
	}


}
