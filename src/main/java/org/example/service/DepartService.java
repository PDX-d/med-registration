package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.DepartDTO;
import org.example.pojo.entity.Depart;

public interface DepartService {
	Result add(DepartDTO departDTO);

	Result list(Long page, Long pageSize, String keyword);

	Result detail(Long id);

	Result updateDepart(DepartDTO departDTO);

	Result delete(Long id);

	Result all();
}
