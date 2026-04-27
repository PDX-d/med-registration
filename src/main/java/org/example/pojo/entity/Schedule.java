package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("schedule")
@AllArgsConstructor
@NoArgsConstructor
public class Schedule  implements Serializable {
	@TableId(type = IdType.AUTO)
	private Long id;

	// 科室ID
	private Long departmentId;

	// 医生ID
	private Long doctorId;

	// 排班日期
	private LocalDate scheduleDate;

	// ========== 你要的 放号时间 ==========
	private LocalDateTime releaseTime;

	// 时段：上午/下午/晚上
	private String timeSlot;

	// 最大预约数
	private Integer maxAppointments;

	// 已预约数
	private Integer remainingCount;

	// 状态 0=停诊 1=正常
	private Integer status;

	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createTime;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

}
