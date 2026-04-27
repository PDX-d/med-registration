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
@TableName("post")
@ApiModel(value = "ConsultArticle对象", description = "咨询文章")
@AllArgsConstructor
@NoArgsConstructor
public class Post {
	@TableId(type = IdType.AUTO)
	private Long id;
	private String title; // 标题
	private String content; // 内容
	private String coverImage; // 封面图
	private Long userId; // 作者ID
	private Integer viewCount; // 浏览量
	private Integer likeCount; // 点赞数
	private Integer favoriteCount; // 收藏数
	private Integer commentCount; // 评论数
	private Integer status; // 状态：0-草稿 1-已发布 2-已删除
	private LocalDateTime createTime;
	private LocalDateTime updateTime;
}
