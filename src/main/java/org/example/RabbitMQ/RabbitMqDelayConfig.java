package org.example.RabbitMQ;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.example.common.constants.RabbitMQConstant.*;

@Configuration
public class RabbitMqDelayConfig {
	// 延迟交换机
	@Bean
	public CustomExchange orderDelayExchange() {
		return new CustomExchange(ORDER_DELAY_EXCHANGE, "x-delayed-message", true, false,
				java.util.Map.of("x-delayed-type", "direct"));
	}

	// 延迟队列
	@Bean
	public Queue orderDelayQueue()  {
		return QueueBuilder.durable(ORDER_DELAY_QUEUE).build();
	}

	// 绑定
	@Bean
	public Binding orderDelayBinding() {
		return BindingBuilder.bind(orderDelayQueue())
				.to(orderDelayExchange())
				.with(ORDER_DELAY_ROUTING_KEY).noargs();
	}

}