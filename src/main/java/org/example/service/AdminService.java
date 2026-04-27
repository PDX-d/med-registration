package org.example.service;

import org.example.common.result.Result;

public interface AdminService {


	Result list(Long page, Long pageSize, String keyword);
}
