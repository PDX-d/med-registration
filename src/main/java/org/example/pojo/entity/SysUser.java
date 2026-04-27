package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表用于登录
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class SysUser  {

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 1正常 0禁用
	 */
	private Integer status;

	//验证码
	@TableField(exist = false)
	private String code;


}

