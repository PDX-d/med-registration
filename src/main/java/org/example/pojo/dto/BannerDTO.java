package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerDTO {
	private Long id;// id
	@NotBlank(message = "描述不能为空")
	private String description;// 描述
	@NotBlank(message = "标题不能为空")
	private String title;// 标题
	@NotBlank(message = "图片不能为空")
	private String imageUrl;// 图片
	@NotNull(message = "排序不能为空")
	private Integer sort;// 排序
	@NotNull(message = "状态不能为空")
	private Integer status;// 状态
}
