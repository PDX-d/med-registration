package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BannerVO {
	private Long id;// id
	private String description;// 描述
	private String title;// 标题
	private String imageUrl;// 图片
	private Integer sort;// 排序
	private Integer status;// 状态
	private LocalDateTime createTime;
}
