package org.example;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.SysPermissionMapper;
import org.example.mapper.SysRoleMapper;
import org.example.mapper.SysRolePermissionMapper;
import org.example.pojo.entity.SysPermission;
import org.example.pojo.entity.SysRole;
import org.example.pojo.entity.SysRolePermission;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.common.constants.RedisConstant.ROLE_PERMS_KEY;

/**
 * RBAC权限初始化测试类
 * 将角色权限关系保存到Redis中
 */
@SpringBootTest
@Slf4j
public class RBACPermissionInitTest {

	@Resource
	private SysRoleMapper sysRoleMapper;

	@Resource
	private SysPermissionMapper sysPermissionMapper;

	@Resource
	private SysRolePermissionMapper sysRolePermissionMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 测试方法：将所有角色的权限加载到Redis中
	 * Redis存储格式：role:perms:{roleName} -> Set<permCode>
	 * 例如：role:perms:ADMIN -> ["user:delete", "user:add", ...]
	 */
	@Test
	public void testInitRolePermissionsToRedis() {
		log.info("========== 开始初始化角色权限到Redis ==========");

		// 1. 查询所有角色
		List<SysRole> roles = sysRoleMapper.selectList(null);
		if (roles == null || roles.isEmpty()) {
			log.warn("未找到任何角色数据");
			return;
		}
		log.info("查询到 {} 个角色", roles.size());

		// 2. 遍历每个角色，获取其权限并保存到Redis
		for (SysRole role : roles) {
			String roleName = role.getRoleName();
			String redisKey = ROLE_PERMS_KEY + roleName;

			// 3. 查询该角色关联的所有权限ID
			LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
			rpWrapper.eq(SysRolePermission::getRoleId, role.getId());
			List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(rpWrapper);

			if (rolePermissions == null || rolePermissions.isEmpty()) {
				log.info("角色 [{}] 没有关联任何权限", roleName);
				continue;
			}

			// 4. 提取权限ID列表
			List<Long> permissionIds = rolePermissions.stream()
					.map(SysRolePermission::getPermId)
					.collect(Collectors.toList());

			// 5. 批量查询权限详情
			List<SysPermission> permissions = sysPermissionMapper.selectBatchIds(permissionIds);

			// 6. 提取权限编码（permCode）
			Set<String> permCodes = permissions.stream()
					.map(SysPermission::getPermCode)
					.collect(Collectors.toSet());

			// 7. 删除旧的Redis数据（避免重复）
			stringRedisTemplate.delete(redisKey);

			// 8. 将权限编码集合保存到Redis的Set结构中
			if (!permCodes.isEmpty()) {
				stringRedisTemplate.opsForSet().add(redisKey, permCodes.toArray(new String[0]));
				log.info("角色 [{}] 的 {} 个权限已保存到Redis，Key: {}", roleName, permCodes.size(), redisKey);
			}
		}

		log.info("========== 角色权限初始化完成 ==========");

		// 9. 验证Redis中的数据
		testVerifyRolePermissionsInRedis();
	}

	/**
	 * 验证Redis中的角色权限数据
	 */
	@Test
	public void testVerifyRolePermissionsInRedis() {
		log.info("========== 验证Redis中的角色权限数据 ==========");

		// 查询所有角色
		List<SysRole> roles = sysRoleMapper.selectList(null);

		for (SysRole role : roles) {
			String roleName = role.getRoleName();
			String redisKey = ROLE_PERMS_KEY + roleName;

			// 从Redis中获取权限集合
			Set<String> permCodes = stringRedisTemplate.opsForSet().members(redisKey);

			if (permCodes != null && !permCodes.isEmpty()) {
				log.info("角色 [{}] 的权限: {}", roleName, permCodes);
			} else {
				log.warn("角色 [{}] 在Redis中没有权限数据", roleName);
			}
		}

		log.info("========== 验证完成 ==========");
	}

	/**
	 * 清空Redis中的角色权限数据
	 */
	@Test
	public void testClearRolePermissionsFromRedis() {
		log.info("========== 清空Redis中的角色权限数据 ==========");

		// 查询所有角色
		List<SysRole> roles = sysRoleMapper.selectList(null);

		int count = 0;
		for (SysRole role : roles) {
			String roleName = role.getRoleName();
			String redisKey = ROLE_PERMS_KEY + roleName;
			Boolean deleted = stringRedisTemplate.delete(redisKey);
			if (Boolean.TRUE.equals(deleted)) {
				count++;
				log.info("已删除角色 [{}] 的权限数据，Key: {}", roleName, redisKey);
			}
		}

		log.info("共删除 {} 个角色的权限数据", count);
		log.info("========== 清空完成 ==========");
	}

	/**
	 * 查看数据库中所有的角色、权限及关联关系
	 */
	@Test
	public void testViewAllRBACData() {
		log.info("========== 数据库RBAC数据概览 ==========");

		// 1. 查看所有角色
		List<SysRole> roles = sysRoleMapper.selectList(null);
		log.info("角色列表:");
		for (SysRole role : roles) {
			log.info("  - ID: {}, 角色名: {}", role.getId(), role.getRoleName());
		}

		// 2. 查看所有权限
		List<SysPermission> permissions = sysPermissionMapper.selectList(null);
		log.info("权限列表:");
		for (SysPermission perm : permissions) {
			log.info("  - ID: {}, 权限名: {}, 权限编码: {}", 
					perm.getId(), perm.getPermName(), perm.getPermCode());
		}

		// 3. 查看角色-权限关联
		List<SysRolePermission> rolePermissions = sysRolePermissionMapper.selectList(null);
		log.info("角色-权限关联:");
		for (SysRolePermission rp : rolePermissions) {
			SysRole role = sysRoleMapper.selectById(rp.getRoleId());
			SysPermission perm = sysPermissionMapper.selectById(rp.getPermId());
			if (role != null && perm != null) {
				log.info("  - 角色 [{}] 拥有权限 [{}]", role.getRoleName(), perm.getPermCode());
			}
		}

		log.info("========== 数据概览完成 ==========");
	}
}
