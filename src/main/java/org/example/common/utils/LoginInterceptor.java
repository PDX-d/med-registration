package org.example.common.utils;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.pojo.dto.UserDTO;
import org.example.common.properties.JwtProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.example.common.constants.MessageConstant.ERR_USER_NOT_LOGIN;
import static org.example.common.constants.MessageConstant.PERMISSION_DENIED;
import static org.example.common.constants.RedisConstant.*;

/**
 * RBAC权限拦截器
 * <p>
 * 基于角色的访问控制（Role-Based Access Control）拦截器，负责：
 * </p>
 * <ul>
 *   <li>1. 检查接口是否需要权限（是否有 @RequirePermission 注解）</li>
 *   <li>2. 验证用户登录状态（从 Redis 查询 token）</li>
 *   <li>3. 校验用户权限（检查角色是否拥有指定权限码）</li>
 *   <li>4. 保存用户信息到 ThreadLocal（UserHolder）</li>
 *   <li>5. 刷新 token 过期时间（滑动过期机制）</li>
 * </ul>
 *
 * <h3>权限数据存储结构：</h3>
 * <pre>
 * Redis Hash: login:token:{uuid}
 *   - key: "login:token:" + token
 *   - value: {id, role, phone, name, ...} 用户信息
 *
 * Redis Set: role:perms:{roleName}
 *   - key: "role:perms:" + roleName (例如: role:perms:admin)
 *   - members: ["department:add", "department:delete", "anno:view", ...]
 * </pre>
 *
 * <h3>权限校验流程：</h3>
 * <pre>
 * 请求 → 检查@RequirePermission注解 → 获取token → 查询Redis用户信息
 *   → 提取角色 → 查询角色权限集合 → 校验权限码 → 放行/拒绝
 * </pre>
 */
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

	private final StringRedisTemplate stringRedisTemplate;
	private final JwtProperties jwtProperties;

	public LoginInterceptor(StringRedisTemplate stringRedisTemplate, JwtProperties jwtProperties) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.jwtProperties = jwtProperties;
	}

	/**
	 * 前置处理：RBAC权限校验
	 * <p>
	 * 在请求到达Controller之前执行，完成以下校验：
	 * </p>
	 * <ol>
	 *   <li>检查方法是否有 @RequirePermission 注解，无注解则直接放行</li>
	 *   <li>从请求头获取 token，验证用户是否登录</li>
	 *   <li>从 Redis 查询用户信息，验证 token 有效性</li>
	 *   <li>根据用户角色查询权限集合，校验是否有权限访问</li>
	 *   <li>权限校验通过后，保存用户信息到 ThreadLocal</li>
	 *   <li>刷新 token 过期时间（滑动窗口机制）</li>
	 * </ol>
	 *
	 * @param request  HTTP请求对象
	 * @param response HTTP响应对象
	 * @param handler  处理器方法
	 * @return true-放行请求，false-拦截请求
	 * @throws Exception 处理异常
	 */
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod method = (HandlerMethod) handler;
		RequirePermission annotation = method.getMethodAnnotation(RequirePermission.class);
		if (annotation == null) {
			log.info("用户不需要权限");
			return true;
		}
		String token = request.getHeader("authorization");
		if (token == null) {
			log.error(ERR_USER_NOT_LOGIN);
			return false;
		}
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		String key = LOGIN_TOKEN_KEY + token;
		Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(key);
		if (userMap.isEmpty()) {
			log.error(ERR_USER_NOT_LOGIN);
			return false;
		}
		UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
		String roleKey = ROLE_PERMS_KEY + userDTO.getRole();
		String requiredPerm = annotation.value();
		Boolean perms = stringRedisTemplate.opsForSet().isMember(roleKey, requiredPerm);
		log.info("当前角色{},接口权限码:{},能否执行{}", userDTO.getRole(), requiredPerm, perms);
		if (Boolean.FALSE.equals(perms)) {
			log.error(PERMISSION_DENIED);
			return false;
		}
		log.info("用户权限正常");
		UserHolder.saveUser(userDTO);
		log.info("用户已登录，用户信息:{}", UserHolder.getUser());
		// 7. 刷新 token 过期时间（滑动窗口机制，避免频繁重新登录）
		stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		UserHolder.removeUser();
	}
}

