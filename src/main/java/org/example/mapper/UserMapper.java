package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.pojo.entity.User;

import java.time.LocalDateTime;

public interface UserMapper extends BaseMapper<User> {
	void updateLoginInfo(Long id, LocalDateTime lastLoginTime, String lastLoginIp);

	void updateSysUserPhone(String newPhone, Long id);
	void updateUserPhone(String newPhone, Long id);
}
