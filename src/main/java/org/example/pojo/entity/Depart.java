package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("depart")
public class Depart {
	@TableId(type = IdType.AUTO) // 数据库自增
	private Long id;
	private String introduction;
	private String location;
	private String name;
	private String phone;
	private Long userId;

	/**
	 * 状态：0-停用，1-正常
	 */
	private Integer status;
	private LocalDateTime createTime;
	private LocalDateTime updateTime;

}
