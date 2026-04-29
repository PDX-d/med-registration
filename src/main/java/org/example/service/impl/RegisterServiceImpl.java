package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.mapper.SysUserRoleMapper;
import org.example.mapper.UserMapper;
import org.example.common.result.Result;
import org.example.pojo.dto.SysUserDTO;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.SysUserRole;
import org.example.pojo.entity.User;
import org.example.mapper.RegisterMapper;
import org.example.service.RegisterService;
import org.example.common.utils.RegexUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.example.common.constants.MessageConstant.*;

@Slf4j
@Service
public class RegisterServiceImpl implements RegisterService {

	@Resource
	private RegisterMapper registerMapper;

	@Resource
	private UserMapper userMapper;

	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	@Resource
	private CopyMapper copyMapper;

	/**
	 * 用户注册
	 * <p>
	 * 注册流程：
	 * 1. 校验手机号格式
	 * 2. 检查账号是否已存在
	 * 3. 设置默认密码（MD5加密）
	 * 4. 设置默认角色和状态
	 * 5. 插入数据库
	 * </p>
	 *
	 * @param sysUserDTO 注册用户信息（包含手机号、密码等）
	 * @return 注册结果
	 */
	@Override
	@Transactional
	public Result register(SysUserDTO sysUserDTO) {
		if (RegexUtils.isPhoneInvalid(sysUserDTO.getPhone())) {
			return Result.fail(PHONE_INVALID);
		}
		// 2. 检查账号是否已存在
		String existUser = registerMapper.register(sysUserDTO);
		if (existUser != null) {
			log.warn("注册失败，手机号已存在: {}", sysUserDTO.getPhone());
			return Result.fail(USER_NAME_EXISTS);
		}
		SysUser sysUser = copyMapper.SysUserDTOToSysUser(sysUserDTO);
		sysUser.setPassword(DEFAULT_PASSWORD);
		sysUser.setStatus(3);
		int insert = registerMapper.insert(sysUser);
		if (insert <= 0) {
			return Result.fail(REGISTER_ERROR);
		}
		// 3. 构建用户对象
		User user = new User();
		user.setId(sysUser.getId());
		user.setPhone(sysUser.getPhone());
		user.setName("用户" + UUID.randomUUID().toString().replace("-", "").substring(24));
		user.setCreateTime(LocalDateTime.now());
		userMapper.insert(user);
		SysUserRole sysUserRole = new SysUserRole(null, sysUser.getId(), 2L);
		sysUserRoleMapper.insert(sysUserRole);
		log.info("用户注册成功，手机号：{}，用户ID：{}", sysUser.getPhone(), sysUser.getId());
		return Result.success();
	}
}
