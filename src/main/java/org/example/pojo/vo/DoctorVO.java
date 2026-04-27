package org.example.pojo.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("doctor")
public class DoctorVO {
	// 主键自增
	private Long id;
	private String username;// 用户名
	private String realName;// 姓名
	private String phone;//手机号
	private Long departmentId;//部门id
	private String email; //邮箱
	private String title; //职称
	private Integer status;
	private String avatar;//头像
	private String introduction;
	private LocalDateTime createTime;
	// 总预约数
	private Integer count;
}
