package org.example.config;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

//布隆过滤器
@Configuration
public class BloomFilterConfig {
	private static int size = 100;//预计要插入多少数据

	private static double fpp = 0.01;//期望的误判率

	@Bean
	public BloomFilter<String> bloomFilter() {
		return BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), size, fpp);
	}
}
