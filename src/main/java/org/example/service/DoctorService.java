package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.DoctorDTO;
import org.example.pojo.entity.Doctor;

public interface DoctorService {
	Result add(DoctorDTO doctorDTO);


	Result listt(Long page, Long pageSize, String keyword, Long departmentId);

	Result getById(Long id);

	Result delete(Long id);

	Result detail(Long id);

	Result update(DoctorDTO doctorDTO);
}
