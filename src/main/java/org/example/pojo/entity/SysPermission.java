package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 权限表
 */
@Data
@TableName("sys_permission")
public class SysPermission {

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 权限名称
	 */
	private String permName;

	/**
	 * 权限标识 如 user:delete
	 */
	private String permCode;
}