package org.example.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.AppointMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class task {
	@Resource
	private AppointMapper appointMapper;
	@Scheduled(fixedRate = 300*1000) // 5mins执行一次
	public void run() {
		log.info("定时任务开始执行检查订单状态是否一致");
		// 只查15分钟内的支付记录
		LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(15);
		int count = appointMapper.checkOrderStatus(timeLimit);
		if (count > 0) {
			log.info("有{}个订单状态不一致，更新状态", count);
		}
	}
}
