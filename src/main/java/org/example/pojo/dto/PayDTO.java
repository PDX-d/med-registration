package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayDTO {
	@NotNull(message = "订单ID不能为空")
	private Long orderId;
	private BigDecimal payAmount;
	private String payMethod;
}
