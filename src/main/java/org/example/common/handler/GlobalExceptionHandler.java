package org.example.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.example.common.exeption.BaseException;
import org.example.common.result.Result;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
@Order(3)
public class GlobalExceptionHandler {

	// 处理业务异常
	@ExceptionHandler(BaseException.class)
	public Result handleRuntimeException(BaseException e) {
		log.error("运行时异常：{}", e.getMessage());
		return Result.fail( e.getMessage());
	}
	/**
	 * 运行时异常（系统未知错误）
	 */
	@ExceptionHandler(RuntimeException.class)
	public Result handleRuntimeException(RuntimeException e) {
		log.error("运行时异常", e);
		return Result.fail("操作失败，请稍后重试");
	}
	/**
	 * 所有其他异常
	 */
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception e) {
		log.error("系统异常", e);
		return Result.fail("系统繁忙，请稍后重试");
	}
	// Redis 连接异常
	@ExceptionHandler(RedisConnectionFailureException.class)
	public Result handleRedisConnectionException(RedisConnectionFailureException e) {
		log.error("Redis 连接异常：{}", e.getMessage());
		// 返回统一友好提示，前端可直接展示
		return Result.fail("系统繁忙，请稍后重试~");
	}
}
