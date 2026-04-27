package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anno {
	@TableId(type = IdType.AUTO)
	private Long id;

	private String title;  // 公告标题

	private Integer type;    // 公告类型 0 紧急 1重要 2 普通

	private String content; // 公告内容

	private Integer status; // 状态：1-发布 0-草稿

	private Long userId;// 操作人ID

	private LocalDateTime updateTime;
	private LocalDateTime createTime;
}
