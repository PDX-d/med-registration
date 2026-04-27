package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.service.HomeService;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "首页")
@Slf4j
@RestController
@RequestMapping("/home")
public class HomeController {
	@Resource
	private HomeService homeService;

	@GetMapping("/banners")
	public Result banners() {
		return homeService.banners();
	}

	@ApiOperation("获取医院官网全局配置")
	@GetMapping("/config")
	public Result getSiteConfig() {
		Map<String, String> config = new HashMap<>();
		config.put("hospitalName", "思远医疗");
		config.put("logoUrl", "https://cdn-icons-png.flaticon.com/128/2966/2966334.png");
		config.put("hospitalPhone", "010-144858");
		config.put("hospitalAddress", "思远医疗");
		config.put("workHours", "周一至周五 8:00-17:30，周六 8:00-12:00");
		config.put("copyright", "Copyright © 2024 XX市第一人民医院 版权所有");
		config.put("icp", "京ICP备12345678号");
		return Result.ok(config);
	}

	@ApiOperation("获取医生列表")
	@GetMapping("/doctor/list")
	public Result doctorList(@RequestParam(required = false) Long departmentId) {
		log.info("获取医生列表");
		return homeService.doctorList(departmentId);
	}

	@ApiOperation("获取公告列表")
	@GetMapping("/announcement/list")
	public Result announcement(@RequestParam(defaultValue = "1") Long page,
							   @RequestParam(defaultValue = "10") Long pageSize,
							   @RequestParam(required = false) Long type
	) {
		return homeService.announcement(page, pageSize, type);
	}

	@ApiOperation("搜索公告")
	@GetMapping("/announcement/search")
	public Result search(@RequestParam String message, Long type) {
		log.info("搜索公告:{},状态{}", message, type);
		return homeService.searchAnno(message, type);
	}
}
