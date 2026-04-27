package org.example.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.example.anno.RequirePermission;
import org.example.pojo.dto.AppointOrderDTO;
import org.example.common.result.Result;
import org.example.pojo.dto.PayDTO;
import org.example.pojo.dto.UserDTO;
import org.example.service.AppointService;
import org.example.common.utils.UserHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static org.example.common.constants.MessageConstant.ERR_USER_NOT_LOGIN;


@Api(tags = "用户预约")
@Slf4j
@Validated
@RestController
@RequestMapping("/appointment")
public class AppointController {

	@Resource
	private AppointService appointService;

	@PostMapping("/make")
	@ApiOperation(value = "创建预约", notes = "用户预约医生排班")
	@RequirePermission("appointment:create")
	public Result makeAppointment(@Valid @RequestBody AppointOrderDTO appointOrderDTO) {
		log.info("用户预约排班:{}", appointOrderDTO);
		return appointService.makeAppointment(appointOrderDTO);
	}

	@GetMapping("/count")
	@ApiOperation(value = "获取预约号数", notes = "查询指定排班的剩余号数")
	public Result count(@NotNull(message = "排班ID不能为空") @RequestParam Long scheduleId) {
		log.info("号数:{}", scheduleId);
		return appointService.AppointCount(scheduleId);
	}

	@GetMapping("/patient/list")
	@RequirePermission("appointment:view")
	@ApiOperation(value = "获取就诊人列表", notes = "获取当前用户的就诊人信息列表")
	public Result list() {
		return appointService.list();
	}

	@PostMapping("/pay")
	@RequirePermission("appointment:pay")
	@ApiOperation(value = "支付订单", notes = "用户支付预约订单")
	public Result pay(@Valid @RequestBody PayDTO payDTO) {
		log.info("用户支付:{}", payDTO);
		return appointService.pay(payDTO);
	}

	@PutMapping("/cancel/{orderId}")
	@ApiOperation(value = "取消预约", notes = "用户取消已创建的预约订单")
	@RequirePermission("appointment:cancel")
	public Result cancel(@NotNull(message = "预约ID不能为空") @PathVariable Long orderId) {
		log.info("取消预约:{}", orderId);
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			log.info("用户未登录");
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		return appointService.cancel(orderId, user.getId());
	}
}
