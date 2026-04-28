package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InfoMapper {


	void updateUsername(String email, String realName,Long id);

	String findEmail(Long id);

	Integer updatePassword(Long id, String newPassword,String oldPassword);
}
