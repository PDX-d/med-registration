package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.AppointOrder;
import org.example.pojo.entity.PayRecord;
import org.example.pojo.vo.AppointOrderVO;
import org.example.pojo.vo.AppointVO;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface AppointMapper extends BaseMapper<AppointOrder> {
	void updateStatus(Map map);

	void addPayRecord(PayRecord payRecord);

	int checkOrderStatus(LocalDateTime payTime);

	AppointOrder selectByOrderId(Long orderId);

	IPage<AppointVO> selectDoctorListPage(
			Page<AppointOrder> pageParam,
			String status,
			String time,
			String keyword,
			Long id);
}
