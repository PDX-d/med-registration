package org.example.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result implements Serializable {
	private Integer code;
	private String errorMsg;
	private Object data;
	private Long total;

	public static Result success() {
		return new Result(200, "操作成功", null, null);
	}

	public static Result success(Object data) {
		return new Result(200, "操作成功", data, null);
	}

	public static Result success(List<?> data, Long total) {
		return new Result(200, "操作成功", data, total);
	}

	public static Result fail(String errorMsg) {
		return new Result(500, errorMsg, null, null);
	}

	public static Result fail(Integer code, String errorMsg) {
		return new Result(code, errorMsg, null, null);
	}
}
