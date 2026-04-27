package org.example.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching // 开启缓存
public class RedisCacheConfig {

	//@Bean
	public RedisCacheManager RedisCacheManager(RedisConnectionFactory factory) {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule()); // 支持Java8时间
		om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
		om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);

		GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(om);
		StringRedisSerializer stringSerializer = new StringRedisSerializer();

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofHours(1))
				.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
				.disableCachingNullValues();

		return RedisCacheManager.builder(factory)
				.cacheDefaults(config)
				.build();
	}
}