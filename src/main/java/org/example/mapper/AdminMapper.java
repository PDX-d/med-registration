package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.User;

@Mapper
public interface AdminMapper extends BaseMapper<SysUser> {

	IPage<UserDTO> selectAdminUserPage(Page<UserDTO> page, @Param("phone") String phone);

}
