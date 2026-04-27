package org.example.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
	/*
	 * 1.引入Redisson
	 * 2.创建一个RedissonClient对象
	 * 3.给容器中添加RedissonClient组件
	 * 4.使用RedissonClient对象操作Redis
	 */
	@Bean
	public RedissonClient redissonClient(){
		Config config = new Config();
		config.useSingleServer().setAddress("redis://localhost:6379");
		return Redisson.create(config);
	}

}
