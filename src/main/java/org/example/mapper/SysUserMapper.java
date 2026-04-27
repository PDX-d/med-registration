package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.pojo.entity.SysUser;

public interface SysUserMapper extends BaseMapper<SysUser> {
	Integer checkPhone(String phone);
}
