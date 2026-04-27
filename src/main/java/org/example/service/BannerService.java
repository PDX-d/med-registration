package org.example.service;

import org.example.common.result.Result;
import org.example.pojo.dto.BannerDTO;
import org.example.pojo.entity.Banner;

public interface BannerService {
	Result add(BannerDTO bannerDTO);

	Result list(Long page, Long pageSize);

	Result detail(Long id);

	Result update(BannerDTO bannerDTO);

	Result delete(Long id);
}
