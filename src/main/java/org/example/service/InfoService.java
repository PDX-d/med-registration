package org.example.service;

import org.example.pojo.dto.Password;
import org.example.common.result.Result;

public interface InfoService {
	Result info();

	Result updateUsername(String email, String realName);

	Result updatePassword(Password password);
}
