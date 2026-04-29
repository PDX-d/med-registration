package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
	@NotBlank(message = "旧密码不能为空")
	private String oldPassword;
	@NotBlank(message = "新密码不能为空")
	private String newPassword;
	@NotBlank(message = "确认密码不能为空")
	private String confirmPassword;
}
