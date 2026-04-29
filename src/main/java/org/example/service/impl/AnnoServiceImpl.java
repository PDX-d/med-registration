package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.common.result.Result;
import org.example.pojo.dto.AnnoDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Anno;
import org.example.mapper.AnnoMapper;
import org.example.mapper.DepartMapper;
import org.example.pojo.vo.AnnoVO;
import org.example.service.AnnoService;
import org.example.common.utils.UserHolder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.*;

@Service
@Slf4j
public class AnnoServiceImpl extends ServiceImpl<AnnoMapper, Anno> implements AnnoService {
	@Resource
	private AnnoMapper annoMapper;

	@Resource
	private DepartMapper departMapper;

	@Resource
	private RedisTemplate redisTemplate;

	@Resource
	private CopyMapper copyMapper;

	@Override
	public Result addAnno(AnnoDTO annoDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Anno anno = copyMapper.AnnoDTOToAnno(annoDTO);
		anno.setUserId(user.getId());
		anno.setCreateTime(LocalDateTime.now());
		anno.setUpdateTime(LocalDateTime.now());
		int isSuccess = annoMapper.insert(anno);
		if (isSuccess < 0) {
			return Result.fail(ANNO_ADD_ERROR);
		}
		return Result.success();
	}

	//分页查询
	@Override
	public Result list(Long page, Long pageSize, String keyword) {
		Page<Anno> listPage = new Page<>(page, pageSize);
		LambdaQueryWrapper<Anno> wrapper = new LambdaQueryWrapper<>();
		if (StrUtil.isNotBlank(keyword)) {
			wrapper.like(Anno::getTitle, keyword);
		}
		wrapper.orderByDesc(Anno::getCreateTime);
		IPage<Anno> resultPage = this.page(listPage, wrapper);
		List<AnnoVO> voList = resultPage.getRecords().stream()
				.map(copyMapper::AnnoToAnnoVO) // 逐个转换
				.collect(Collectors.toList());
		return Result.success(voList, resultPage.getTotal());
	}

	@Override
	public Result detail(Long id) {
		log.info("获取公告详情:{}", id);
		Anno anno = annoMapper.selectById(id);
		if (anno == null) {
			return Result.fail(ANNO_NOT_FOUND);
		}
		AnnoVO annoVO = copyMapper.AnnoToAnnoVO(anno);
		return Result.success(annoVO);
	}

	@Override
	public Result updateAnno(AnnoDTO annoDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Anno anno = copyMapper.AnnoDTOToAnno(annoDTO);
		if(anno ==null){
			return Result.fail(ANNO_NOT_FOUND);
		}
		anno.setUpdateTime(LocalDateTime.now());
		int isSuccess = annoMapper.updateById(anno);
		if (isSuccess == 0) {
			return Result.fail(UPDATE_ERROR);
		}
		return Result.success();
	}

	@Override
	public Result delete(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		int isSuccess = annoMapper.deleteById(id);
		if (isSuccess == 0) {
			return Result.fail("删除失败");
		}
		return Result.success();
	}
}
