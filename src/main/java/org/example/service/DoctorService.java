package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.DoctorDTO;

public interface DoctorService {
	Result add(DoctorDTO doctorDTO);


	Result list(Long page, Long pageSize, String keyword, Long departmentId);

	Result getById(Long id);

	Result delete(Long id);

	Result detail(Long id);

	Result update(DoctorDTO doctorDTO);
}
