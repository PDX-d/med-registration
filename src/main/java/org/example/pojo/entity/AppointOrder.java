package org.example.pojo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.pojo.dto.UserDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("appoint_order")
public class AppointOrder {
	private Long orderId;            // 订单Id

	private Long patientId;            // 患者ID

	private Long departId;               // 科室ID
	private Long doctorId;             // 医生ID
	private Long scheduleId;           // 排班ID

	private LocalDate scheduleDate;    // 就诊日期
	private String timeSlot;           // 就诊时段

	private BigDecimal fee;            // 挂号费
	private Integer payStatus;         // 0未支付 1已支付
	private Integer orderStatus;       // 1预约成功 2取消 3已就诊 4过期

	private LocalDateTime createTime;
	private LocalDateTime payTime;
	private LocalDateTime payDeadline;// 支付截止时间

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;

	@TableField(exist = false)
	private UserDTO user;
}
