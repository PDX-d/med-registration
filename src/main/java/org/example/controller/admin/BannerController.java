package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.BannerDTO;
import org.example.pojo.entity.Banner;
import org.example.service.BannerService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@Api(tags = "图片管理")
@Slf4j
@RestController
@RequestMapping("/site/banner")
@Validated
public class BannerController {

	@Resource
	private BannerService bannerService;

	@PostMapping("/add")
	@RequirePermission("banner:add")
	@ApiOperation(value = "添加轮播图", notes = "新增一个轮播图")
	public Result add(@Valid  @RequestBody BannerDTO bannerDTO) {
		log.info("添加轮播图");
		return bannerService.add(bannerDTO);
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取轮播图列表", notes = "分页获取轮播图列表")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize
	) {
		log.info("获取轮播图列表");
		return bannerService.list(page, pageSize);
	}

	@GetMapping("/detail/{id}")
	@ApiOperation(value = "获取轮播图详情", notes = "根据ID获取轮播图详细信息")
	public Result detail(@PathVariable Long id) {
		log.info("获取轮播图详情，id: {}", id);
		return bannerService.detail(id);
	}

	@PutMapping("/update")
	@RequirePermission("banner:update")
	@ApiOperation(value = "修改轮播图", notes = "更新轮播图信息")
	public Result update(@Valid @RequestBody BannerDTO bannerDTO) {
		log.info("修改轮播图: {}", bannerDTO);
		return bannerService.update(bannerDTO);
	}

	@DeleteMapping("/delete/{id}")
	@RequirePermission("banner:delete")
	@ApiOperation(value = "删除轮播图", notes = "根据ID删除轮播图")
	public Result delete(@NotNull(message = "id不能为空") @PathVariable Long id) {
		log.info("删除轮播图，id: {}", id);
		return bannerService.delete(id);
	}
}
