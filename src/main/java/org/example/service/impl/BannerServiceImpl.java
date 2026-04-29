package org.example.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.mapper.BannerMapper;
import org.example.common.result.Result;
import org.example.pojo.dto.BannerDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Banner;
import org.example.pojo.vo.BannerVO;
import org.example.service.BannerService;
import org.example.common.utils.UserHolder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.UPLOAD_ERROR;
import static org.example.common.constants.MessageConstant.USER_NOT_LOGIN;
import static org.example.common.constants.RedisConstant.HOME_KEY;

@Service
@Slf4j
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

	@Resource
	private BannerMapper bannerMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private CacheManager cacheManager;
	@Resource
	private CopyMapper copyMapper;

	@Override
	public Result add(BannerDTO bannerDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Banner banner = copyMapper.BannerDTOToBanner(bannerDTO);
		banner.setUserId(user.getId());
		banner.setCreateTime(LocalDateTime.now());
		banner.setUpdateTime(LocalDateTime.now());
		int isSuccess = bannerMapper.insert(banner);
		if (isSuccess < 0) {
			return Result.fail(UPLOAD_ERROR);
		}
		clearDepartCache();
		return Result.success();
	}

	@Override
	public Result list(Long page, Long pageSize) {
		Page<Banner> listPage = new Page<>(page, pageSize);
		IPage<Banner> bannerIPage = this.page(listPage, null);
		List<BannerVO> BannerList = bannerIPage.getRecords().stream().map(
				banner -> copyMapper.BannerToBannerVO(banner)
		).collect(Collectors.toList());
		return Result.success(BannerList, bannerIPage.getTotal());
	}

	@Override
	public Result detail(Long id) {
		log.info("获取轮播图详情，id: {}", id);
		Banner banner = bannerMapper.selectById(id);
		BannerVO bannerVO = copyMapper.BannerToBannerVO(banner);
		if (bannerVO == null) {
			return Result.fail("轮播图不存在");
		}
		return Result.success(bannerVO);
	}

	@Override
	public Result update(BannerDTO bannerDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		Banner banner = copyMapper.BannerDTOToBanner(bannerDTO);
		banner.setUserId(user.getId());
		banner.setUpdateTime(LocalDateTime.now());
		int isSuccess = bannerMapper.updateById(banner);
		if (isSuccess <= 0) {
			return Result.fail("更新失败");
		}
		clearDepartCache();
		return Result.success();
	}

	@Override
	public Result delete(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(USER_NOT_LOGIN);
		}
		int isSuccess = bannerMapper.deleteById(id);
		if (isSuccess <= 0) {
			return Result.fail("删除失败");
		}
		clearDepartCache();
		return Result.success();
	}

	/**
	 * 统一清理缓存（Redis + 本地Caffeine）
	 */
	private void clearDepartCache() {
		String cacheKey = "cache:" + HOME_KEY;
		// 1. 删除Redis缓存
		stringRedisTemplate.delete(HOME_KEY);
		// 2. 删除Caffeine本地缓存
		Cache cache = cacheManager.getCache(cacheKey);
		if (cache != null) {
			cache.clear();
		}
	}
}
