package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.anno.PhoneDesensitize;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointVO {
	private Long id;
	private String patientName;
	// 手机号脱敏
	@PhoneDesensitize
	private String patientPhone;
	private String doctorName;
	private String timeSlot;
	private String departmentName;
	private Integer status;
	private LocalDate appointmentDate;
	private LocalDateTime createTime;
	private LocalDateTime confirmTime;
	private String cancelRole;
	private String cancelReason;
}
