package org.example.pojo.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleVO {

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
}
