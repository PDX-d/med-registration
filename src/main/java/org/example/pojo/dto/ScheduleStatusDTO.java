package org.example.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleStatusDTO {
	@NotNull(message = "ID不能为空")
	private Long id;
	@NotNull(message = "状态不能为空")
	private Integer status;
}
