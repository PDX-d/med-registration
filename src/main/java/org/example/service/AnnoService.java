package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.result.Result;
import org.example.pojo.dto.AnnoDTO;
import org.example.pojo.entity.Anno;

public interface AnnoService extends IService<Anno> {

	Result addAnno(AnnoDTO anno);

	Result list( Long page ,Long pageSize, String keyword);

	Result detail(Long id);

	Result updateAnno(AnnoDTO annoDTO);

	Result delete(Long id);

}
