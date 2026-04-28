package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.SysUserDTO;
import org.example.pojo.entity.SysUser;

public interface RegisterService {
	Result register(SysUserDTO user);
}
