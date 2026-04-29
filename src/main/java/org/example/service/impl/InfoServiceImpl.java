package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.common.utils.DesensitizeUtil;
import org.example.mapper.SysUserMapper;
import org.example.mapper.UserMapper;
import org.example.pojo.dto.*;
import org.example.common.result.Result;
import org.example.mapper.InfoMapper;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.User;
import org.example.pojo.vo.UserVO;
import org.example.service.InfoService;
import org.example.common.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.*;
import static org.example.common.constants.RedisConstant.LOGIN_TOKEN_KEY;
import static org.example.common.constants.RedisConstant.LOGIN_USER_KEY;

@Service
@Slf4j
public class InfoServiceImpl extends ServiceImpl<UserMapper, User> implements InfoService {
	@Resource
	private InfoMapper infoMapper;

	@Resource
	private UserMapper userMapper;
	@Resource
	private CopyMapper copyMapper;
	@Resource
	private LoginServiceImpl loginService;
	@Resource
	private SysUserMapper sysUserMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;


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
		return Result.success(userVO);
	}

	@Override
	public Result updatePassword(PasswordDTO passwordDTO) {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		if (passwordDTO.getConfirmPassword().equals(passwordDTO.getNewPassword())) {
			return Result.fail(PASSWORD_NOT_SAME);
		}
		int update = infoMapper.updatePassword(userDTO.getId(), passwordDTO.getNewPassword(), passwordDTO.getOldPassword());
		if (update <= 0) {
			return Result.fail(PASSWORD_MODIFY_ERROR);
		}
		return Result.success();
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
		return Result.success();
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
		return Result.success();
	}

	@Override
	public Result list(Long page, Long pageSize, String keyword) {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Page<User> userPage = new Page<>(page, pageSize);
		LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
		if (StrUtil.isNotBlank(keyword)) {
			queryWrapper.like(User::getName, keyword);
		}
		IPage<User> userIPage = this.page(userPage, queryWrapper);
		List<UserVO> collect = userIPage.getRecords()
				.stream()
				.map(user -> copyMapper.UserToUserVO(user))
				.peek(userVO -> {
					SysUser sysUser = sysUserMapper.selectById(userVO.getId());
					userVO.setStatus(sysUser.getStatus());
					userVO.setPhone(DesensitizeUtil.phone(userVO.getPhone()));
					userVO.setIdCard(DesensitizeUtil.idCard(userVO.getIdCard()));
				}).collect(Collectors.toList());
		return Result.success(collect, userIPage.getTotal());
	}

	@Override
	public Result updateStatus(UpdateStatus updateStatus) {
		UserDTO userDTO = UserHolder.getUser();
		if (userDTO == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		SysUser sysUser = sysUserMapper.selectById(updateStatus.getId());
		if (sysUser == null) {
			return Result.fail(USER_NOT_EXIST);
		}
		if (sysUser.getStatus().equals(updateStatus.getStatus())) {
			return Result.fail(USER_STATUS_NOT_CHANGE);
		}
		sysUser.setStatus(updateStatus.getStatus());
		int update = sysUserMapper.updateById(sysUser);
		if (update <= 0) {
			return Result.fail(UPDATE_ERROR);
		}
		String userTokenKey = LOGIN_USER_KEY + updateStatus.getId();
		// 1. 获取该用户所有 token
		Set<String> tokens = stringRedisTemplate.opsForSet().members(userTokenKey);
		if (tokens == null || tokens.isEmpty()) {
			stringRedisTemplate.delete(userTokenKey);
			return Result.success();
		}
		// 2. 批量删除所有 token
		for (String token : tokens) {
			String tokenKey = LOGIN_TOKEN_KEY + token;
			stringRedisTemplate.delete(tokenKey);
		}
		// 3. 删除用户的 token 集合
		stringRedisTemplate.delete(userTokenKey);
		return Result.success();
	}
}
