package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointCancelDTO {
	private Long orderId;
	private Long scheduleId;
	private Long patientId;
}
