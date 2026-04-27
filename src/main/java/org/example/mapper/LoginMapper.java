package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.User;
import org.example.pojo.vo.LoginUserVO;

@Mapper
public interface LoginMapper extends BaseMapper<SysUser> {

	SysUser login(SysUser user);

	LoginUserVO loginWithRoles(SysUser sysUser);
}
