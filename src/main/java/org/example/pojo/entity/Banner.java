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
@TableName("banner")
@ApiModel(value = "Banner对象", description = "轮播图")
@AllArgsConstructor
@NoArgsConstructor
public class Banner {
	@TableId(type = IdType.AUTO) // 数据库自增
	private Long id;// id
	private Long userId;// 用户id
	private String description;// 描述
	private String title;// 标题
	private String imageUrl;// 图片
	private Integer sort;// 排序
	private Integer status;// 状态
	private LocalDateTime createTime;// 创建时间
	// 更新时间
	private LocalDateTime updateTime;
}
