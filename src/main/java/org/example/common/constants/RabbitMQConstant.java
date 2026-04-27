package org.example.common.constants;

/**
 * RabbitMQ 常量类
 */
public class RabbitMQConstant {
	/**
	 * 预约订单队列
	 */
	public static final String APPOINT_ORDER_QUEUE = "appointOrderQueue"; // 预约订单队列
	public static final String APPOINT_ORDER_EXCHANGE = "appointOrderExchange"; // 预约订单交换机
	public static final String APPOINT_ORDER_ROUTING_KEY = "appointOrderRoutingKey"; // 预约订单路由键
	public static final String APPOINT_ORDER = "appointOrder"; // 预约订单标识

	/**
	 * 延迟队列配置
	 */
	public static final String ORDER_DELAY_EXCHANGE = "orderDelayExchange"; // 延迟交换机
	public static final String ORDER_DELAY_QUEUE = "orderDelayQueue"; // 延迟队列
	public static final String ORDER_DELAY_ROUTING_KEY = "orderDelayRouting"; // 路由键

	/**
	 * 订单超时时间（阶梯延迟）
	 */
	public static final long[] DELAY_TIME_LEVELS = {
			10 * 1000,    // 0: 10秒
			30 * 1000,    // 1: 30秒
			60 * 1000,    // 2: 1分钟
			180 * 1000,   // 3: 3分钟
			620 * 1000    // 4: 10分钟 20秒
	};
}
