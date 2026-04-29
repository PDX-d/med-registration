package org.example.listener;

import lombok.extern.slf4j.Slf4j;
import org.example.pojo.dto.OrderDelayMessageDTO;
import org.example.mapper.AppointMapper;
import org.example.service.AppointService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.example.common.constants.OrderConstant.*;
import static org.example.common.constants.RabbitMQConstant.*;
import static org.example.common.constants.RedisConstant.APPOINT_ORDER_STATUS_KEY;

@Component
@Slf4j
public class AppointOrderDelayConsumer {

	@Resource
	private AppointMapper appointMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@Resource
	private AppointService appointService;


	@RabbitListener(queues = ORDER_DELAY_QUEUE)
	public void handle(OrderDelayMessageDTO message) {
		Long orderId = message.getOrderId();
		int retryCount = message.getRetryCount();
		log.info("收到订单延迟检查 → orderId={}, 重试次数={}", orderId, retryCount);
		String key = APPOINT_ORDER_STATUS_KEY + orderId;

		// 1. 查询订单是否支付
		String isPay = stringRedisTemplate.opsForValue().get(key);
		if (isPay == null) {
			log.info("支付状态{},订单已取消,不再检查{}", null, orderId);
			return;
		}
		if (String.valueOf(PAY_STATUS_PAID).equals(isPay)) {
			log.info("订单已支付,不再检查{}", orderId);
			stringRedisTemplate.delete(key);
			return;
		}
		if (String.valueOf(ORDER_STATUS_CANCEL).equals(isPay)) {
			log.info("订单已取消,不再检查{}", orderId);
			stringRedisTemplate.delete(key);
			return;
		}
		// 3. 判断是否到达最大次数 → 超时取消
		if (retryCount >= 4) {
			log.info("订单支付超时取消{}", orderId);
			appointService.cancel(orderId, message.getUserId());
			return;
		}
		// 2. 订单未支付，检查是否超时
		sendNextDelayMessage(orderId, retryCount + 1);
	}

	/**
	 * 发送下一次延迟消息
	 */
	private void sendNextDelayMessage(Long orderId, int nextRetryCount) {
		OrderDelayMessageDTO nextMessage = new OrderDelayMessageDTO();
		nextMessage.setOrderId(orderId);
		nextMessage.setRetryCount(nextRetryCount);
		long delay = DELAY_TIME_LEVELS[nextRetryCount];
		rabbitTemplate.convertAndSend(
				ORDER_DELAY_EXCHANGE,
				ORDER_DELAY_ROUTING_KEY,
				nextMessage,
				msg -> {
					msg.getMessageProperties().setDelay((int) delay);
					return msg;
				}
		);
		log.info("发送下一次检查 → orderId={}, 次数={}, 延迟={}ms",
				orderId, nextRetryCount, delay);
	}
}
