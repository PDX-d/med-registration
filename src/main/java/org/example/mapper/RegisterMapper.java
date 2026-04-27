package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.User;

@Mapper
public interface RegisterMapper extends BaseMapper<SysUser> {
	SysUser register(SysUser user);

	void addUser(User user);
}
