package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("comment")
@ApiModel(value = "Comment对象", description = "评论")
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long postId; // 文章ID
	private Long userId; // 用户ID
	private String content; // 评论内容
	private Long parentId; // 父评论ID（回复评论时使用）
	private Integer likeCount; // 点赞数
	private Integer status; // 状态：0-禁用 1-正常
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}
