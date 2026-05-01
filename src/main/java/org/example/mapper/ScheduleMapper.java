package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.dto.ScheduleStatusDTO;
import org.example.pojo.entity.Schedule;

@Mapper
public interface ScheduleMapper extends BaseMapper<Schedule> {

	Integer updateCount(Schedule schedule);

	void updateCountUp(Long scheduleId);

	void isDelete(Long id);

	void updateStatus(ScheduleStatusDTO scheduleDTO);
}
