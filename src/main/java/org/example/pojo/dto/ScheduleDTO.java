package org.example.pojo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDTO {

	private Long id;

	@NotNull(message = "科室ID不能为空")
	private Long departmentId;

	@NotNull(message = "医生ID不能为空")
	private Long doctorId;

	// 排班日期
	@NotNull(message = "排班日期不能为空")
	private LocalDate scheduleDate;

	@NotBlank(message = "时段不能为空")
	private String timeSlot;

	// 最大预约数
	@NotNull(message = "最大预约数不能为空")
	private Integer maxAppointments;

	// 状态 0=停诊 1=正常
	@NotNull(message = "状态不能为空")
	private Integer status;
}
