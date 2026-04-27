package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.DepartDTO;
import org.example.pojo.entity.Depart;
import org.example.service.DepartService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(tags = "部门管理")
@Slf4j
@RestController
@RequestMapping("/department")
@Validated
public class DepartController {

	@Resource
	private DepartService departService;

	@PostMapping("/add")
	@RequirePermission("department:add")
	@ApiOperation(value = "添加部门", notes = "新增一个科室部门")
	public Result add(@Valid  @RequestBody DepartDTO DepartDTO) {
		log.info("添加部门:{}", DepartDTO);
		return departService.add(DepartDTO);
	}

	//分页查询
	@GetMapping("/list")
	@ApiOperation(value = "获取部门列表", notes = "分页获取部门列表，支持关键词搜索")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String keyword
	) {
		return departService.list(page, pageSize, keyword);
	}


	@GetMapping("/detail/{id}")
	@ApiOperation(value = "获取部门详情", notes = "根据ID获取部门详细信息")
	public Result detail(@PathVariable Long id) {
		return departService.detail(id);
	}

	//修改公告
	@PutMapping("/update")
	@RequirePermission("department:update")
	@ApiOperation(value = "修改部门", notes = "更新部门信息")
	public Result update(@Valid @RequestBody DepartDTO departDTO) {
		log.info("修改:{}", departDTO);
		return departService.updateDepart(departDTO);
	}

	//删除公告
	@DeleteMapping("/delete/{id}")
	@RequirePermission("department:delete")
	@ApiOperation(value = "删除部门", notes = "根据ID删除部门")
	public Result delete(@NotNull(message = "ID不能为空") @PathVariable Long id) {
		log.info("删除:{}", id);
		return departService.delete(id);
	}

	@GetMapping("/all")
	@ApiOperation(value = "获取所有部门", notes = "获取全部部门列表")
	public Result all() {
		log.info("获取所有部门");
		return departService.all();
	}
}
