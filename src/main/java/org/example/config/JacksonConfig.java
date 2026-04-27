package org.example.config;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

	private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

	@Bean
	public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();

		// 序列化 → 转字符串给前端
		builder.serializerByType(LocalDateTime.class,
				new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(PATTERN)));

		// 反序列化 → 接收前端传的字符串
		builder.deserializerByType(LocalDateTime.class,
				new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(PATTERN)));

		return builder;
	}
}