package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.pojo.entity.AppointOrder;
import org.example.pojo.entity.PayRecord;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface AppointMapper extends BaseMapper<AppointOrder> {
	void updateStatus(Map map);

	void addPayRecord(PayRecord payRecord);

	int checkOrderStatus(LocalDateTime payTime);

	AppointOrder selectByOrderId(Long orderId);
}
