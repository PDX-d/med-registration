package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InofMapper {


	void updateUsername(String email, String realName,Long id);

	String findemail(Long id);

	Integer updatePassword(Long id, String newPassword,String oldPassword);
}
