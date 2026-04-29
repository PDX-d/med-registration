package org.example.service;

import org.example.pojo.dto.PasswordDTO;
import org.example.common.result.Result;
import org.example.pojo.dto.PhoneUpdateDTO;
import org.example.pojo.dto.UpdateStatus;
import org.example.pojo.dto.UserInfoDTO;

public interface InfoService {
	Result info();


	Result updatePassword(PasswordDTO passwordDTO);

	Result updateInfo(UserInfoDTO userInfoDTO);

	Result updatePhone(PhoneUpdateDTO phoneUpdateDTO);

	Result list(Long page, Long pageSize, String keyword);

	Result updateStatus(UpdateStatus updateStatus);
}
