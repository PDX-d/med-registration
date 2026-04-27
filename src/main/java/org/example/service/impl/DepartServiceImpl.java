package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.pojo.dto.DepartDTO;
import org.example.common.result.Result;
import org.example.pojo.entity.Depart;
import org.example.mapper.DepartMapper;
import org.example.pojo.vo.DepartVO;
import org.example.service.DepartService;
import org.example.common.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import org.springframework.cache.CacheManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.*;
import static org.example.common.constants.RedisConstant.DEPART_ALL_KEY;

@Service
@Slf4j
public class DepartServiceImpl extends ServiceImpl<DepartMapper, Depart> implements DepartService {
	@Resource
	private DepartMapper departMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private CacheManager cacheManager;

	@Resource
	private CopyMapper copyMapper;

	//添加
	@Override
	public Result add(DepartDTO departDTO) {
		Long userId = UserHolder.getUser().getId();
		if (userId == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Depart depart = copyMapper.DepartDTOTodepart(departDTO);
		depart.setUserId(userId);
		depart.setCreateTime(LocalDateTime.now());
		depart.setUpdateTime(LocalDateTime.now());
		int isSuccess = departMapper.insert(depart);
		if (isSuccess == 0) {
			return Result.fail(DEPT_ADD_ERROR);
		}
		clearDepartCache();
		return Result.ok();
	}

	//分页查询
	@Override
	public Result list(Long page, Long pageSize, String keyword) {
		Page<Depart> listPage = new Page<>(page, pageSize);
		LambdaQueryWrapper<Depart> wrapper = new LambdaQueryWrapper<>();
		if (StrUtil.isNotBlank(keyword)) {
			wrapper.like(Depart::getName, keyword);
		}
		wrapper.orderByDesc(Depart::getCreateTime);
		IPage<Depart> resultPage = this.page(listPage, wrapper);
		List<DepartVO> resultList = resultPage.getRecords().stream()
				.map(copyMapper::DepartToDepartVO)
				.collect(Collectors.toList());
		return Result.ok(resultList, resultPage.getTotal());
	}

	@Override
	public Result detail(Long id) {
		log.info("获取部门详情");
		Depart depart = departMapper.selectById(id);
		if (depart == null) {
			return Result.fail(DEPT_NOT_FOUND);
		}
		DepartVO departVO = copyMapper.DepartToDepartVO(depart);
		return Result.ok(departVO);
	}

	@Override
	public Result updateDepart(DepartDTO departDTO) {
		Long userId = UserHolder.getUser().getId();
		if (userId == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Depart depart = copyMapper.DepartDTOTodepart(departDTO);
		int isSuccess = departMapper.updateById(depart);
		if (isSuccess < 0) {
			return Result.fail(UPDATE_ERROR);
		}
		clearDepartCache();
		return Result.ok();
	}

	@Override
	public Result delete(Long id) {
		Long userId = UserHolder.getUser().getId();
		if (userId == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		int isSuccess = departMapper.deleteById(id);
		if (isSuccess < 0) {
			return Result.fail(DELETE_ERROR);
		}
		clearDepartCache();
		return Result.ok();
	}

	/**
	 * 统一清理缓存（Redis + 本地Caffeine）
	 */
	private void clearDepartCache() {
		String cacheKey = "cache:" + DEPART_ALL_KEY;
		// 1. 删除Redis缓存
		stringRedisTemplate.delete(DEPART_ALL_KEY);
		// 2. 删除Caffeine本地缓存
		Cache cache = cacheManager.getCache(cacheKey);
		if (cache != null) {
			cache.clear();
		}
	}

	@Override
	public Result all() {
		String cacheKey = "cache:" + DEPART_ALL_KEY;
		//检查Cache
		Cache cache = cacheManager.getCache(cacheKey);
		if (cache.get(cacheKey) != null) {
			log.info("从Cache中获取数据");
			List<DepartDTO> listDTO = (List<DepartDTO>) cache.get(cacheKey).get();
			return Result.ok(listDTO);
		}

		//检查Redis
		String json = stringRedisTemplate.opsForValue().get(DEPART_ALL_KEY);
		if (StrUtil.isNotBlank(json)) {
			log.info("从Redis中获取数据");
			List<DepartDTO> listDTO = JSONUtil.toList(json, DepartDTO.class);
			// 同步到Caffeine，下次直接本地拿
			cache.put(cacheKey, listDTO);
			return Result.ok(listDTO);
		}
		List<Depart> list = departMapper.selectList(null);
		if (list == null) {
			stringRedisTemplate.opsForValue().set(DEPART_ALL_KEY, "", 10, TimeUnit.SECONDS);
			return Result.ok("数据库没有数据");
		}
		//转换Dto
		List<DepartDTO> listDTO = list.stream()
				.map(item -> {
					// 创建DTO对象
					DepartDTO dto = new DepartDTO();
					BeanUtils.copyProperties(item, dto);
					// 有多少属性就写多少，一一对应
					return dto;
				}).collect(Collectors.toList()); // Java 16+ 用 toList()
		json = JSONUtil.toJsonStr(listDTO);
		//放进Cache
		cache.put(cacheKey, listDTO);
		//放进Redis
		stringRedisTemplate.opsForValue().set(DEPART_ALL_KEY, json, 30, TimeUnit.MINUTES);
		return Result.ok(listDTO);
	}
}
