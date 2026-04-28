package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVO {
	private Long id;
	private String phone;
	private Integer status;
	private String roleNames;
	private String name;
	private LocalDateTime lastLoginTime;
}
