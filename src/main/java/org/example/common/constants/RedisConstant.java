package org.example.common.constants;

/**
 * Redis Key 常量类
 */
public class RedisConstant {
	/**
	 * 用户认证相关
	 */
	public static final String LOGIN_CODE_KEY = "login:code:"; // 登录验证码
	public static final String LOGIN_TOKEN_KEY = "login:token:"; // 登录令牌
	public static final String USERID = "userId"; // 用户id
	public static final String ROLE_PERMS_KEY = "role:perms:";
	public static final String LOGIN_USER_KEY = "login:user:";

	/**
	 * 业务数据缓存
	 */
	public static final String ANNO_KEY = "anno:"; // 公告key
	public static final String AI_HISTORY_KEY = "ai:history:"; // AI对话历史
	public static final String DOCTOR_SCHEDULE_KEY = "doctor:schedule:"; // 医生排班
	public static final String DEPART_ALL_KEY = "depart:all:"; // 科室所有医生
	public static final String DEPART_DOCTOR_KEY = "depart:doctor:"; // 科室所有医生
	public static final String APPOINT_ORDER_KEY = "appoint:order:"; // 预约订单
	public static final String APPOINT_ORDER_STATUS_KEY = "appoint:order:status:"; // 预约订单支付状态

	/**
	 * 消息
	 */

	public static final String REDIS_YES = "成功从Redis中获取数据";
	/**
	 * 主页
	 */
	public static final String HOME_KEY = "home:";
	public static final String HOME_ANNO_KEY = "home:anno:";


	/**
	 * TTL 配置
	 */
	public static final Long LOGIN_USER_TTL = 72000000L; // 登录用户Token过期时间
	public static final Long HOT_TTL = 60 * 60 * 24L;
	public static final Long HOME_TTL = 60 * 60 * 24L;
	public static final Long JWT_TTL = 720000000L; // JWT Token过期时间


}
