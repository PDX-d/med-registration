package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointOrderDTO implements Serializable {
	private Long orderId;

	@NotNull(message = "排班ID不能为空")
	private Long scheduleId;

	@NotNull(message = "科室ID不能为空")
	private Long departId;

	@NotNull(message = "医生ID不能为空")
	private Long doctorId;

	private UserDTO userDTO;
}
