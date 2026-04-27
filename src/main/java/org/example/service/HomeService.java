package org.example.service;

import org.example.common.result.Result;

public interface HomeService {
	Result banners();

	Result doctorList(Long departmentId);

	Result announcement(Long page, Long pageSize, Long type);

	Result searchAnno(String message, Long type);
}
