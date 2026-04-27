package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user")
public class User {

	private Long id;
	/**
	 * 姓名
	 */
	private String name;

	/**
	 * 手机号
	 */
	@NotBlank(message = "密码不能为空")
	//@Size(min = 11, max = 11, message = "手机号长度必须为11位")
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
	 * 创建时间
	 */
	private LocalDateTime createTime;

}
