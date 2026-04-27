package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("user_like")
@AllArgsConstructor
@NoArgsConstructor
public class UserLike {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId; // 用户ID
	private Long postId; // 文章ID
	private Integer type; // 类型：1-文章点赞 2-评论点赞
	private LocalDateTime createTime;
}
