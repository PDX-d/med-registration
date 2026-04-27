package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.Anno;

import java.util.List;

@Mapper
public interface AnnoMapper extends BaseMapper<Anno> {

	List<Anno> selectLimit(Long offset, Long pageSize, Long type);
}
