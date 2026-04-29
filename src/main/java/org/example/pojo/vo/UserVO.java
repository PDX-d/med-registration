package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
	private Long id;
	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 头像URL
	 */
	private String avatar;

	/**
	 * 0未知 1男 2女
	 */
	private Integer gender;
	/**
	 * 身份证号码
	 */
	private String idCard;

	private String realName;
	private Integer status;


}
