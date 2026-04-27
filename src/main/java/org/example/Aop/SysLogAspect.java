package org.example.Aop;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.anno.SysLog;
import org.example.pojo.dto.UserDTO;
import org.example.common.utils.UserHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect// 切面类
@Component
@Slf4j
public class SysLogAspect {
	/**
	 * 切点：所有加了 @SysLog 注解的方法
	 */
	@Pointcut("@annotation(org.example.anno.SysLog)")
	public void logPointCut() {
	}

	// 环绕通知日志
	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();

		// 1. 获取方法上的 @SysLog 注解（核心：拿到所有你设置的值）
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		SysLog sysLog = method.getAnnotation(SysLog.class);

		// ============= 读取注解里的所有值 =============
		String desc = sysLog.value();           // 方法描述
		String module = sysLog.module();        // 模块
		String operatorType = sysLog.operatorType(); // 操作类型
		boolean recordParams = sysLog.recordParams(); // 是否记录入参
		boolean recordResult = sysLog.recordResult(); // 是否记录返回值

		// 2. 获取当前登录用户（你项目里的UserHolder，直接用！）
		UserDTO user = UserHolder.getUser();
		String userId = user != null ? user.getId().toString() : "未登录";
		//String username = user != null ? user.getRealName() : "匿名用户";

		// 3. 方法基础信息
		String className = point.getTarget().getClass().getName();
		String methodName = method.getName();
		Object[] args = point.getArgs();

		// ============= 打印日志 =============
		log.info("==================== 日志开始 ====================");
		log.info("业务模块：{}", module);
		log.info("操作类型：{}", operatorType);
		log.info("方法描述：{}", desc);
		log.info("操作人ID：{}", userId);
		log.info("类路径：{}", className);
		log.info("方法名：{}", methodName);

		// 根据配置决定是否打印入参
		if (recordParams) {
			log.info("入参：{}", JSONUtil.toJsonStr(args));
		}

		// 执行目标方法
		Object result = point.proceed();

		// 耗时
		long time = System.currentTimeMillis() - beginTime;
		log.info("执行耗时：{} ms", time);

		// 根据配置决定是否打印出参
		if (recordResult) {
			log.info("返回值：{}", JSONUtil.toJsonStr(args));
		}
		log.info("==================== 日志结束 ====================\n");
		return result;
	}

	//登录验证



}
