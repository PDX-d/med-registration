package org.example.service.impl;

import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserMapper;
import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;
import org.example.mapper.LoginMapper;
import org.example.pojo.vo.LoginUserVO;
import org.example.common.properties.JwtProperties;
import org.example.service.LoginService;
import org.example.common.utils.RegexUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.example.common.constants.MessageConstant.*;
import static org.example.common.constants.RedisConstant.*;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {

	@Resource
	private LoginMapper loginMapper;

	@Resource
	private JwtProperties jwtProperties;

	@Resource
	private UserMapper userMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;


	private Boolean verifyLoginCode(SysUser sysUser) {
		//校验验证码
		String keyCode = LOGIN_CODE_KEY + sysUser.getPhone();
		String code = stringRedisTemplate.opsForValue().get(keyCode);
		if (code == null || !code.equals(sysUser.getCode())) {
			return false;
		}
		// 验证通过后立即删除验证码
		stringRedisTemplate.delete(keyCode);
		return true;
	}

	@Override
	public Result Login(SysUser user) {
		//手机号格式错误
		if (RegexUtils.isPhoneInvalid(user.getPhone())) {
			return Result.fail(PHONE_INVALID);
		}
		if (!verifyLoginCode(user)) {
			return Result.fail(LOGIN_CODE_ERROR);
		}
		//获取登录用户
		LoginUserVO loginUserVO = loginMapper.loginWithRoles(user);
		if (loginUserVO == null) {
			return Result.fail(LOGIN_ERROR);
		}
		//账号禁用
		if (loginUserVO.getStatus() == 0) {
			return Result.fail(ACCOUNT_LOCKED);
		}
		// 4. 生成 Token 并缓存
		String token = UUID.randomUUID().toString().replace("-", "");
		String keyToken = LOGIN_TOKEN_KEY + token;
		Map<String, Object> map = new HashMap<>();
		map.put("id", String.valueOf(loginUserVO.getId()));
		map.put("name", loginUserVO.getName());
		map.put("phone", loginUserVO.getPhone());
		map.put("role", loginUserVO.getRoleNames());
		// 使用 Pipeline 保证 Redis 操作的原子性
		stringRedisTemplate.executePipelined(new SessionCallback<Object>() {
			@Override
			public Object execute(RedisOperations operations) throws DataAccessException {
				operations.opsForHash().putAll(keyToken, map);
				operations.expire(keyToken, LOGIN_USER_TTL, TimeUnit.MINUTES);
				return null;
			}
		});
		map.put("token", token);
		log.info("用户已登录，用户信息:{}", map);
		return Result.ok(map);
	}

	//发送验证码
	@Override
	public Result sendCode(String phone) {
		//生成验证码
		String code = RandomUtil.randomNumbers(6);
		String key = LOGIN_CODE_KEY + phone;
		stringRedisTemplate.opsForValue().set(key, code, 1, TimeUnit.MINUTES);
		log.info("生成验证码:{}", code);
		return Result.ok(code);
	}

	/**
	 * 退出登录
	 * <p>
	 * 退出流程：
	 * 1. 从 UserHolder 获取当前登录用户信息
	 * 2. 从请求头获取 Token
	 * 3. 删除 Redis 中的用户 Token 缓存
	 * 4. 清除 ThreadLocal 中的用户信息
	 * </p>
	 *
	 * @return 退出结果
	 */
	@Override
	public Result logout() {

		return Result.ok("退出成功");
	}
}
