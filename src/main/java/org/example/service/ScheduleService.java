package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.ScheduleDTO;
import org.example.pojo.dto.ScheduleStatusDTO;
import org.example.pojo.entity.Schedule;

public interface ScheduleService {


	Result add(ScheduleDTO scheduleDTO);

	Result list(Long page, Long pageSize, String departmentId,String doctorId, String date);

	Result detail(Long id);

	Result update(ScheduleDTO scheduleDTO);

	Result delete(Long id);

	Result listById(Long doctorId);

	Result getCurrentUser();

	Result updateStatus(ScheduleStatusDTO scheduleDTO);
}
