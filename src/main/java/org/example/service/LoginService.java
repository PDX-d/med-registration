package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {

	Result Login(SysUser user, HttpServletRequest  request);

	Result sendCode(String phone);

	Result logout();
}
