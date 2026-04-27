package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.pojo.entity.Doctor;

import java.util.List;
import java.util.Map;


@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {

	@MapKey("id")
	List<Map<String, Object>> selectBatchByIds(@Param("ids")List<Long> ids);

	List<Doctor> hot();
}
