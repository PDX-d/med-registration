package org.example.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.pojo.dto.UserDTO;
import org.example.mapper.AdminMapper;
import org.example.service.AdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

	@Resource
	private AdminMapper adminMapper;

	@Override
	public Result list(Long page, Long pageSize, String keyword) {
		log.info("获取管理列表");
		Page<UserDTO> listPage = new Page<>(page, pageSize);
		IPage<UserDTO> adminUserPage = adminMapper.selectAdminUserPage(listPage, keyword);
		return Result.ok(adminUserPage.getRecords(), adminUserPage.getTotal());
	}
}
