package org.example.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class History {
	private String role;
	private String content;
	private String time;


	private String reasoning;
	private List<String> questions;
	private Boolean showReasoning;
}
