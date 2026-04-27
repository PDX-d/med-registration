package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayRecord {
	private Long orderId;
	private LocalDateTime payTime;
	private String payStatus;
}
