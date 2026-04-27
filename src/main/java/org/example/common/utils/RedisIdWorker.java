package org.example.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class RedisIdWorker {
	//全局id生成器
	// 起始时间戳
	private static final long BEGIN_TIMESTAMP = 1640995200L;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	public long nextId(String keyPrefix) {
		// 生成时间戳
		LocalDateTime now = LocalDateTime.now();
		long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
		long timestamp = nowSecond - BEGIN_TIMESTAMP;
		// 生成日期
		String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		//生成序列号
		long count = stringRedisTemplate.opsForValue().increment("icr:" + keyPrefix + ":" + date);
		//拼接并返回
		return timestamp << 32 | count;
	}

}
