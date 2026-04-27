package org.example.RabbitMQ;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.common.constants.RabbitMQConstant.*;

@Configuration
public class RabbitMQConfig {
	// 1. 声明队列
	@Bean
	public Queue appointOrderQueue() {
		// true 表示持久化，重启不丢失
		return new Queue(APPOINT_ORDER_QUEUE, true);
	}

	// 2. 声明交换机（Direct模式：精准匹配）
	@Bean
	public DirectExchange appointOrderExchange() {
		return new DirectExchange(APPOINT_ORDER_EXCHANGE);
	}

	// 3. 绑定：队列 + 交换机 + 路由key
	@Bean
	public Binding appointOrderBinding() {
		return BindingBuilder
				.bind(appointOrderQueue())
				.to(appointOrderExchange())
				.with(APPOINT_ORDER_ROUTING_KEY);
	}
}
