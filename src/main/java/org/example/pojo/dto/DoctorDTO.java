package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDTO {
	private Long id;
	@NotBlank(message = "用户名不能为空")
	private String username;// 用户名
	@NotBlank(message = "姓名不能为空")
	private String realName;// 姓名
	@NotBlank(message = "手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式错误")
	private String phone;//手机号
	@NotNull(message = "部门id不能为空")
	private Long departmentId;//部门id
	@Pattern(regexp = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$", message = "邮箱格式错误")
	private String email; //邮箱
	@NotBlank(message = "职称不能为空")
	private String title; //职称
	private String introduction;
	private String avatar;//头像
}
