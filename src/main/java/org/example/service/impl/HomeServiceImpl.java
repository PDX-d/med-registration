package org.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.hash.BloomFilter;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.AnnoMapper;
import org.example.mapper.BannerMapper;
import org.example.mapper.DoctorMapper;
import org.example.mapper.HomeMapper;
import org.example.common.result.Result;
import org.example.pojo.entity.Anno;
import org.example.pojo.entity.Banner;
import org.example.pojo.entity.Doctor;
import org.example.service.HomeService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.example.common.constants.MessageConstant.ERR_PARAM_ERROR;
import static org.example.common.constants.RedisConstant.*;

@Slf4j
@Service
public class HomeServiceImpl implements HomeService {
	@Resource
	private HomeMapper homeMapper;

	@Resource
	private BannerMapper bannerMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private CacheManager cacheManager;
	@Resource
	private BloomFilter bloomFilter;

	@Resource
	private DoctorMapper doctorMapper;
	@Resource
	private AnnoMapper annoMapper;

	@Override
	public Result banners() {
		log.info("获取轮播图");
		List<Banner> banners = getThreeLevelCache(
				"Cache" + HOME_KEY,
				HOME_KEY,
				Banner.class,
				() -> {
					LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
					wrapper.eq(Banner::getStatus, 1);
					return bannerMapper.selectList(wrapper);
				}
		);
		return Result.success(banners);
	}

	@Override
	public Result doctorList(Long departmentId) {
		log.info("获取医生列表，科室ID:{}", departmentId);
		boolean isTure = bloomFilter.mightContain(String.valueOf(departmentId));
		if (!isTure) {
			log.info("布隆拦截科室不存在");
			return Result.fail("布隆拦截科室不存在");
		}
		String redisKey = DEPART_DOCTOR_KEY + departmentId;
		List<Doctor> doctors = getThreeLevelCache(
				"cache:" + redisKey,
				redisKey,
				Doctor.class,
				() -> {
					if (departmentId != -1) {
						LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
						wrapper.eq(Doctor::getStatus, 1).eq(Doctor::getDepartmentId, departmentId);
						return doctorMapper.selectList(wrapper);
					} else {
						return doctorMapper.hot();
					}
				}
		);
		return Result.success(doctors);
	}

	@Override
	public Result announcement(Long page, Long pageSize, Long type) {
		String redisKey = HOME_ANNO_KEY + "page:" + page + "pageSize:" + pageSize + "type:" + type;
		Long offset = (page - 1) * pageSize;
		List<Anno> doctors = getThreeLevelCache(
				"cache:" + redisKey,
				redisKey,
				Anno.class,
				() -> {
					return annoMapper.selectLimit(offset, pageSize, type);
				}
		);
		return Result.success(doctors);
	}

	@Override
	public Result searchAnno(String message, Long type) {
		if (StrUtil.isBlank(message)) {
			return Result.fail(ERR_PARAM_ERROR);
		}
		LambdaQueryWrapper<Anno> wrapper = new LambdaQueryWrapper<>();
		if (type != null) {
			wrapper.eq(Anno::getType, type);
		}
		wrapper.and(w -> w.like(Anno::getTitle, message)
				.or()
				.like(Anno::getContent, message)
		);
		List<Anno> anno = annoMapper.selectList(wrapper);
		return Result.success(anno);
	}

	@PostConstruct
	public void initBloomFilter() {
		List<Doctor> allDoctors = doctorMapper.selectList(null); // 查询所有医生数据
		// 提取所有不重复的科室ID存入布隆过滤器
		allDoctors.stream()
				.map(Doctor::getDepartmentId)
				.distinct()
				.forEach(departmentId -> bloomFilter.put(String.valueOf(departmentId)));

		System.out.println("布隆过滤器预热完成，共插入：" + allDoctors.stream().map(Doctor::getDepartmentId).distinct().count() + " 个科室ID");
		bloomFilter.put("-1");
	}

	private <T> List<T> getThreeLevelCache(String cacheName, String redisKey, Class<T> clazz, Supplier<List<T>> dbSupplier) {
		// ========== 1. 一级缓存：本地缓存 Caffeine ==========
		Cache cache = cacheManager.getCache(cacheName);
		if (cache != null) {
			Cache.ValueWrapper valueWrapper = cache.get(redisKey);
			if (valueWrapper != null) {
				log.info("【本地缓存】获取数据，key:{}", redisKey);
				return (List<T>) valueWrapper.get();
			}
		}

		// ========== 2. 二级缓存：Redis ==========
		String json = stringRedisTemplate.opsForValue().get(redisKey);
		if (StrUtil.isNotBlank(json)) {
			log.info("【Redis缓存】获取数据，key:{}", redisKey);
			List<T> list = JSONUtil.toList(json, clazz);
			// 写入本地缓存
			if (cache != null) cache.put(redisKey, list);
			return list;
		}
		// ========== 3. 三级缓存：数据库 ==========
		log.info("【数据库】获取数据，key:{}", redisKey);
		List<T> list = dbSupplier.get();

		// 缓存空值，防止穿透
		if (CollUtil.isEmpty(list)) {
			list = Collections.emptyList();
		}
		// 写入双缓存
		stringRedisTemplate.opsForValue().set(redisKey, JSONUtil.toJsonStr(list), HOT_TTL, TimeUnit.SECONDS);
		if (cache != null) cache.put(redisKey, list);
		return list;
	}
}
