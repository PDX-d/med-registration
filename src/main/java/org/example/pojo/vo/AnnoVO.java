package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnoVO {

	private Long id;

	private String title;

	private Integer type;

	private String content;

	private Integer status;

	private LocalDateTime createTime;

}