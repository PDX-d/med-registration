package org.example.anno;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(java.lang.annotation.ElementType.METHOD)
public @interface SysLog {
	/**
	 * 方法描述（例如：订单取消、预约挂号、支付接口）
	 */
	/**
	 * 【核心】方法描述/业务名称 (必填)
	 * 例：用户取消订单、预约挂号、订单支付
	 */
	String value() default "";

	/**
	 * 【扩展】业务模块
	 * 例：预约模块、订单模块、支付模块
	 */
	String module() default "";

	/**
	 * 【扩展】操作类型
	 * 例：CREATE(新增)、CANCEL(取消)、PAY(支付)、QUERY(查询)
	 */
	String operatorType() default "";

	/**
	 * 【扩展】是否记录入参 (默认开启)
	 */
	boolean recordParams() default true;

	/**
	 * 【扩展】是否记录返回值 (默认开启)
	 */
	boolean recordResult() default true;

	// 日志等级（info/debug/error）
	String level() default "info";

	// 是否存入数据库（做操作日志审计）
	boolean saveDb() default false;
}
