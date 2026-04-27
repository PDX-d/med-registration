package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.AnnoDTO;
import org.example.pojo.entity.Anno;
import org.example.service.AnnoService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(tags = "公告管理")
@Slf4j
@RestController
@RequestMapping("/announcement")
@Validated
public class AnnoController {

	@Resource
	private AnnoService annoService;

	//添加公告
	@PostMapping("/add")
	@RequirePermission("anno:add")
	@ApiOperation(value = "添加公告", notes = "新增一条公告信息")
	public Result add(@Valid @RequestBody AnnoDTO annoDTO) {
		log.info("添加公告:{}", annoDTO);
		return annoService.addAnno(annoDTO);
	}

	//分页查询
	@GetMapping("/list")
	@ApiOperation(value = "获取公告列表", notes = "分页获取公告列表，支持关键词搜索")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String keyword
	) {
		log.info("获取公告列表");
		return annoService.list(page, pageSize, keyword);
	}

	//获取公告详情
	@GetMapping("/detail/{id}")
	@ApiOperation(value = "获取公告详情", notes = "根据ID获取公告详细信息")
	public Result detail(@PathVariable Long id) {
		log.info("获取公告详情:{}", id);
		return annoService.detail(id);
	}

	//修改公告
	@PutMapping("/update")
	@RequirePermission("anno:update")
	@ApiOperation(value = "修改公告", notes = "更新公告信息")
	public Result update(@Valid @RequestBody AnnoDTO annoDTO) {
		log.info("修改公告:{}", annoDTO);
		return annoService.updateAnno(annoDTO);
	}

	//删除公告
	@DeleteMapping("/delete/{id}")
	@RequirePermission("anno:delete")
	@ApiOperation(value = "删除公告", notes = "根据ID删除公告")
	public Result delete(@NotNull(message = "ID不能为空") @PathVariable Long id) {
		log.info("删除公告:{}", id);
		return annoService.delete(id);
	}
}
