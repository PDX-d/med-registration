package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointStatus {
	private Long orderId;
	private Integer status;
	private String cancelReason;
	private String cancelRole;
	private LocalDateTime confirmTime;
	private LocalDateTime payTime;
	private LocalDateTime cancelTime;
	private LocalDateTime updateTime;
}
