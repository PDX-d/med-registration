package org.example.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.common.mapstruct.CopyMapper;
import org.example.common.result.Result;
import org.example.mapper.SysUserMapper;
import org.example.mapper.SysUserRoleMapper;
import org.example.pojo.dto.DoctorDTO;
import org.example.pojo.dto.UserDTO;
import org.example.pojo.entity.Doctor;
import org.example.mapper.DoctorMapper;
import org.example.mapper.LoginMapper;
import org.example.pojo.entity.SysUser;
import org.example.pojo.entity.SysUserRole;
import org.example.pojo.vo.DoctorVO;
import org.example.service.DoctorService;
import org.example.common.utils.UserHolder;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.common.constants.MessageConstant.*;
import static org.example.common.constants.RedisConstant.DEPART_DOCTOR_KEY;

@Service
@Slf4j
public class DoctorServiceImpl extends ServiceImpl<DoctorMapper, Doctor> implements DoctorService {
	@Resource
	private DoctorMapper doctorMapper;

	@Resource
	private LoginMapper loginMapper;

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Resource
	private CopyMapper copyMapper;
	@Resource
	private SysUserMapper sysUserMapper;
	@Resource
	private SysUserRoleMapper sysUserRoleMapper;
	@Resource
	private CacheManager cacheManager;

	@Override
	@Transactional
	public Result add(DoctorDTO doctorDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		int isSuccess = sysUserMapper.checkPhone(doctorDTO.getPhone());
		if (isSuccess > 0) {
			return Result.fail(USER_EXISTS);
		}
		SysUser sysUser = new SysUser();
		sysUser.setPhone(doctorDTO.getPhone());
		sysUser.setPassword(DEFAULT_PASSWORD);
		sysUser.setStatus(STATUS_ENABLE);
		sysUserMapper.insert(sysUser);
		Doctor doctor = copyMapper.DoctorDTOToDoctor(doctorDTO);
		doctor.setId(sysUser.getId());
		doctor.setCount(0);
		doctor.setCreateTime(LocalDateTime.now());
		doctor.setUpdateTime(LocalDateTime.now());
		int insert = doctorMapper.insert(doctor);
		if (insert <= 0) {
			return Result.fail("添加失败");
		}
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setUserId(sysUser.getId());
		sysUserRole.setRoleId(2L);
		sysUserRoleMapper.insert(sysUserRole);
		clearDepartCache(sysUser.getId());
		return Result.success();
	}

	@Override
	public Result listt(Long page, Long pageSize, String keyword, Long departmentId) {
		Page<Doctor> listPage = new Page<>(page, pageSize);
		LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
		// 标题模糊查询
		if (StrUtil.isNotBlank(keyword)) {
			wrapper.like(Doctor::getRealName, keyword)
					.or()
					.like(Doctor::getPhone, keyword);
		}
		// 3.2 科室精准筛选（下拉框选择）
		if (departmentId != null) {
			wrapper.eq(Doctor::getDepartmentId, departmentId);
		}
		wrapper.orderByDesc(Doctor::getCreateTime);
		IPage<Doctor> resultPage = this.page(listPage, wrapper);
		List<DoctorVO> collectList = resultPage.getRecords().stream()
				.map(copyMapper::DoctorToDoctorVO) // 逐个转换
				.collect(Collectors.toList());
		return Result.success(collectList, resultPage.getTotal());
	}

	@Override
	public Result getById(Long id) {
		String key = DEPART_DOCTOR_KEY + id;
		String json = stringRedisTemplate.opsForValue().get(key);
		if (StrUtil.isNotBlank(json)) {
			log.info("从Redis中获取数据");
			List<Doctor> doctorList = JSONUtil.toList(json, Doctor.class);
			return Result.success(doctorList);
		}
		LambdaQueryWrapper<Doctor> wrapper = new LambdaQueryWrapper<>();
		wrapper.eq(Doctor::getDepartmentId, id);
		wrapper.eq(Doctor::getStatus, STATUS_ENABLE);
		List<Doctor> doctorList = doctorMapper.selectList(wrapper);

		if (doctorList == null || doctorList.isEmpty()) {
			return Result.fail("该科室下暂无医生");
		}
		json = JSONUtil.toJsonStr(doctorList);
		stringRedisTemplate.opsForValue().set(key, json);
		return Result.success(doctorList);
	}

	@Override
	@Transactional
	public Result delete(Long id) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		// 1. 先查到医生信息（拿到 userId）
		Doctor doctor = doctorMapper.selectById(id);
		if (doctor == null) {
			return Result.fail("医生不存在");
		}
		// 3. 删除医生
		int isSuccess = doctorMapper.deleteById(id);
		if (isSuccess <= 0) {
			return Result.fail(DELETE_ERROR);
		}
		sysUserMapper.deleteById(id);
		stringRedisTemplate.delete(DEPART_DOCTOR_KEY + doctor.getDepartmentId());
		clearDepartCache(id);
		return Result.success();
	}

	@Override
	public Result detail(Long id) {
		Doctor doctor = doctorMapper.selectById(id);
		if (doctor == null) {
			return Result.fail("医生不存在");
		}
		DoctorVO doctorVO = copyMapper.DoctorToDoctorVO(doctor);
		return Result.success(doctorVO);
	}

	@Override
	public Result update(DoctorDTO doctorDTO) {
		UserDTO user = UserHolder.getUser();
		if (user == null) {
			return Result.fail(ERR_USER_NOT_LOGIN);
		}
		Doctor doctor = copyMapper.DoctorDTOToDoctor(doctorDTO);
		doctor.setUpdateTime(LocalDateTime.now());
		int update = doctorMapper.updateById(doctor);
		if (update <= 0) {
			return Result.fail(UPDATE_ERROR);
		}
		clearDepartCache(doctor.getDepartmentId());
		return Result.success();
	}

	private void clearDepartCache(Long departmentId) {
		String redisKey = DEPART_DOCTOR_KEY + departmentId;
		String cacheKey = "cache:" + redisKey;
		// 1. 删除Redis缓存
		stringRedisTemplate.delete(redisKey);
		// 2. 删除Caffeine本地缓存
		Cache cache = cacheManager.getCache(cacheKey);
		if (cache != null) {
			cache.clear();
		}
	}
}
