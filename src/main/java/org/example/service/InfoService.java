package org.example.service;

import org.example.pojo.dto.Password;
import org.example.common.result.Result;
import org.example.pojo.dto.PhoneUpdateDTO;
import org.example.pojo.dto.UserInfoDTO;

public interface InfoService {
	Result info();


	Result updatePassword(Password password);

	Result updateInfo(UserInfoDTO userInfoDTO);

	Result updatePhone(PhoneUpdateDTO phoneUpdateDTO);
}
