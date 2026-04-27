package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctor")
public class Doctor {
	// 主键自增
	@TableId(type = IdType.AUTO)
	private Long id;
	private String username;// 用户名
	private String realName;// 姓名
	private String phone;//手机号
	private Long departmentId;//部门id
	private String email; //邮箱
	private String title; //职称
	private Integer status;
	private String avatar;//头像
	// 简介
	private String introduction;

	// 创建时间（插入时自动填充）
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;
	// 更新时间（更新时自动填充）
	@TableField(fill = FieldFill.UPDATE)
	private LocalDateTime updateTime;
	// 总预约数
	private Integer count;

}
