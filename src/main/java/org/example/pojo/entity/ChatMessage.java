package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
	private List<History> history;
	@NotBlank(message = "请输入内容")
	private String message;
	private String userId;
}
