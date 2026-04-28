package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneUpdateDTO {
	// 旧手机号
	@NotBlank(message = "旧手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "原手机号格式错误")
	private String oldPhone;
	// 新手机号
	@NotBlank(message = "新手机号不能为空")
	@Pattern(regexp = "^1[3-9]\\d{9}$", message = "新手机号格式错误")
	private String newPhone;
	// 新手机验证码
	@NotBlank(message = "新手机验证码不能为空")
	private String code;
}
