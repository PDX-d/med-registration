package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;

public interface LoginService {

	Result Login(SysUser user);

	Result sendCode(String phone);

	Result logout();
}
