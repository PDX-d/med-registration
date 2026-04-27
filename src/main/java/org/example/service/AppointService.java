package org.example.service;

import org.example.pojo.dto.AppointOrderDTO;
import org.example.common.result.Result;
import org.example.pojo.dto.PayDTO;

public interface AppointService {
	Result makeAppointment(AppointOrderDTO appointOrderDTO);

	Result AppointCount(Long scheduleId);

	Result list();

	Result pay(PayDTO payDTO);

	Result cancel(Long orderId, Long userId);
}
