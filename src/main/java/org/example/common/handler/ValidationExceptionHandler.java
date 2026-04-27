package org.example.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;

@Order(2)
@Slf4j
@RestControllerAdvice// 全局捕获Controller抛出的异常
public class ValidationExceptionHandler {

	/**
	 * 处理@RequestBody + @Validated 校验失败异常（JSON请求体）
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handle(MethodArgumentNotValidException e) {
		FieldError fieldError = e.getBindingResult().getFieldError();
		String message = fieldError != null ? fieldError.getDefaultMessage() : "参数格式错误";
		log.error("Body参数验证异常：{}", message);
		return Result.fail(message);
	}
	/**
	 * 处理@RequestParam/@PathVariable + @Validated 校验失败异常（Query/Path参数）
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public Result handle(ConstraintViolationException e) {
		// 拼接所有校验失败提示（多参数场景）
		String message = e.getConstraintViolations().stream()
				.map(ConstraintViolation::getMessage)
				.collect(Collectors.joining("；"));
		log.warn("路径/查询参数校验失败：{}", message, e);
		return Result.fail(message);
	}

	/**
	 * 处理@Validated 校验失败异常 类型错误
	 * @param e
	 * @return
	 */
	@ExceptionHandler(ConstraintDeclarationException.class)
	public Result handle(ConstraintDeclarationException e) {
		String message = e.getCause() != null ? e.getCause().getMessage() : "类型错误";
		log.warn("类型错误：{}", message, e);
		return Result.fail(message);
	}

	/**
	 * 处理DTO绑定失败异常（如类型不匹配）
	 */
	@ExceptionHandler(BindException.class)
	public Result handle(BindException e) {
		String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
		log.warn("参数绑定失败：{}", message, e);
		return Result.fail(message);
	}
}
