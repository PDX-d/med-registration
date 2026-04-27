package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartVO {
	private Long id;
	private String name;
	private String introduction;
	private String location;
	private String phone;
	private Integer status;
}
