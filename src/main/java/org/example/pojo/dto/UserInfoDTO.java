package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {
	// 用户信息
	private Long id;
	// 用户头像
	private String avatar;
	// 用户姓名
	@NotBlank(message = "用户姓名不能为空")
	private String name;
	// 真实姓名
	@NotBlank(message = "真实姓名不能为空")
	private String realName;
	// 性别
	@NotNull(message = "性别不能为空")
	private Integer gender;
}
