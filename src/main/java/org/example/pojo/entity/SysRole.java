package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 角色表
 */
@Data
@TableName("sys_role")
public class SysRole {

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 角色标识
	 */
	private String roleName;

	/**
	 * 角色描述
	 */
	private String roleDesc;
}