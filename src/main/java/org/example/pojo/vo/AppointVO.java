package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointVO {
	private Long id;
	private String patientName;
	private String patientPhone;
	private String timeSlot;
	private String departmentName;
	private Integer status;
	private LocalDate appointmentDate;
	private LocalDateTime createTime;
}
