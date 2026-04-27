package org.example.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartDTO {

	private Long id;
	/**
	 * 科室名称
	 */
	@NotBlank(message = "科室名称不能为空")
	private String name;
	/**
	 * 科室简介
	 */
	@NotBlank(message = "科室简介不能为空")
	private String introduction;
	/**
	 * 科室地址
	 */
	@NotBlank(message = "科室地址不能为空")
	private String location;
	/**
	 * 科室电话
	 */
	@NotBlank(message = "科室电话不能为空")
	private String phone;
	/**
	 * 状态：0-停用，1-正常
	 */
	@NotNull(message = "状态不能为空")
	private Integer status;
}
