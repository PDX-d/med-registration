package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDelayMessageDTO implements Serializable {
	private Long orderId;       // 订单ID
	private Long userId;    // 用户ID
	private Integer retryCount; // 当前重试次数
}
