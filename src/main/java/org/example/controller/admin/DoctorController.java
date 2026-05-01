package org.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.common.result.Result;
import org.example.pojo.dto.DoctorDTO;
import org.example.service.DoctorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Api(tags = "医生管理")
@Slf4j
@RestController
@Validated
@RequestMapping("/doctor")
public class DoctorController {

	@Resource
	private DoctorService doctorService;

	@PostMapping("/register")
	@RequirePermission("doctor:register")
	@ApiOperation(value = "注册医生", notes = "新增医生信息")
	public Result register(@Valid @RequestBody DoctorDTO doctorDTO) {
		log.info("注册{}", doctorDTO);
		return doctorService.add(doctorDTO);
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取医生列表", notes = "分页获取医生列表，支持关键词和科室筛选")
	public Result list(@RequestParam(defaultValue = "1") Long page,
					   @RequestParam(defaultValue = "10") Long pageSize,
					   @RequestParam(required = false) String keyword,
					   @RequestParam(required = false) Long departmentId
	) {
		log.info("获取所有医生列表");
		return doctorService.list(page, pageSize, keyword, departmentId);
	}

	@GetMapping("/department/{id}")
	@ApiOperation(value = "根据科室获取医生", notes = "获取指定科室的医生列表")
	public Result getById(@NotNull(message = "科室ID不能为空") @PathVariable Long id) {
		log.info("获取医生信息: {}", id);
		return doctorService.getById(id);
	}

	@DeleteMapping("/delete/{id}")
	@RequirePermission("doctor:delete")
	@ApiOperation(value = "删除医生", notes = "根据ID删除医生信息")
	public Result delete(@NotNull(message = "ID不能为空") @PathVariable Long id) {
		log.info("删除医生: {}", id);
		return doctorService.delete(id);
	}

	@GetMapping("/detail/{id}")
	@ApiOperation(value = "获取医生详情", notes = "根据ID获取医生详细信息")
	public Result detail(@NotNull(message = "ID不能为空") @PathVariable Long id) {
		log.info("获取医生信息: {}", id);
		return doctorService.detail(id);
	}

	@PutMapping("/update")
	@RequirePermission("doctor:update")
	@ApiOperation(value = "更新医生信息", notes = "更新医生信息")
	public Result update(@Valid @RequestBody DoctorDTO doctorDTO) {
		log.info("更新医生信息: {}", doctorDTO);
		return doctorService.update(doctorDTO);
	}
}
