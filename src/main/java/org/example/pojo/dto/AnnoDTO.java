package org.example.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnoDTO {
	private Long id;

	@NotBlank(message = "公告标题不能为空")
	@Size(min = 4, max = 100, message = "标题长度在5-100")
	private String title;  // 公告标题

	@NotNull(message = "公告类型不能为空")
	private Integer type;    // 公告类型 0 紧急 1重要 2 普通

	@NotBlank(message = "公告内容不能为空")
	@Size(min = 10, max = 1000, message = "内容长度在10-1000")
	private String content; // 公告内容

	@NotNull(message = "状态不能为空")
	private Integer status; // 状态：1-发布 0-草稿

}