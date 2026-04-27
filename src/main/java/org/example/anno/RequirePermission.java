package org.example.anno;

import org.example.common.utils.LoginInterceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * RBAC权限校验注解
 * <p>
 * 用于标记需要特定权限才能访问的接口方法。
 * 配合 {@link LoginInterceptor} 使用，在请求到达Controller之前进行权限校验。
 * </p>
 * 
 * <h3>使用示例：</h3>
 * <pre>{@code
 * @PostMapping("/add")
 * @RequirePermission("department:add")
 * public Result add(@RequestBody Depart depart) {
 *     return departService.add(depart);
 * }
 * }</pre>
 * 
 * <h3>权限标识格式：</h3>
 * <ul>
 *   <li>部门管理：department:view, department:add, department:update, department:delete</li>
 *   <li>医生管理：doctor:view, doctor:register, doctor:delete</li>
 *   <li>排班管理：schedule:view, schedule:add, schedule:update, schedule:delete</li>
 *   <li>轮播图管理：banner:view, banner:add, banner:update, banner:delete</li>
 *   <li>公告管理：anno:view, anno:add, anno:update, anno:delete</li>
 *   <li>预约管理：appointment:view, appointment:create, appointment:cancel, appointment:pay</li>
 *   <li>患者管理：patient:view</li>
 * </ul>
 * 
 * <h3>权限校验流程：</h3>
 * <ol>
 *   <li>从请求头获取 token</li>
 *   <li>从 Redis 中查询用户信息（LOGIN_TOKEN_KEY + token）</li>
 *   <li>根据用户角色构建权限Key（ROLE_PERMS_KEY + roleName）</li>
 *   <li>检查 Redis Set 中是否包含所需权限码</li>
 *   <li>无权限则返回 403，有权限则放行并刷新 token 过期时间</li>
 * </ol>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePermission {
	/**
	 * 权限标识符
	 * <p>
	 * 格式：模块:操作，例如：department:add、anno:delete
	 * </p>
	 * 
	 * @return 权限编码，对应 sys_permission 表中的 perm_code 字段
	 */
	String value();
}
