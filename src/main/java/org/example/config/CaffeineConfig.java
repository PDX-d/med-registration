package org.example.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

	@Bean
	public CaffeineCacheManager cacheManager() {
		System.out.println("cacheManager 启动");
		CaffeineCacheManager cacheManager = new CaffeineCacheManager();

		// 缓存核心配置
		cacheManager.setCaffeine(Caffeine.newBuilder()
				.maximumSize(1000)// 最大缓存条数
				.expireAfterWrite(5, TimeUnit.MINUTES)// 写入5分钟过期
				.expireAfterAccess(10, TimeUnit.MINUTES) // 最后一次访问10分钟过期
				.recordStats()             // 开启缓存统计（命中率、命中数）
		);
		return cacheManager;
	}
}
