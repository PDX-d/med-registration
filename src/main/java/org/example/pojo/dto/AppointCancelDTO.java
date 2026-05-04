package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointCancelDTO {
	@NotNull(message = "预约ID不能为空")
	private Long id;
	@NotBlank(message = "取消原因不能为空")
	private String cancelReason;
	private LocalDateTime cancelTime;


}
