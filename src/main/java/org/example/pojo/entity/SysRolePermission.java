package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色权限关联表
 */
@Data
@TableName("sys_role_permission")
public class SysRolePermission {

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 角色ID
	 */
	private Long roleId;

	/**
	 * 权限ID
	 */
	private Long permId;
}