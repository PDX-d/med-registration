package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Post;

@Mapper
public interface ConsultMapper extends BaseMapper<Post> {
}
