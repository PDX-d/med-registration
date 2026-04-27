package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("user_favorite")
@AllArgsConstructor
@NoArgsConstructor
public class UserFavorite {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId; // 用户ID
	private Long postId; // 文章ID
	private LocalDateTime createTime;
}
