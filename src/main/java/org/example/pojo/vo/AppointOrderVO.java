package org.example.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointOrderVO {
	// ==================== 预约ID ====================
	private Long orderId;                    // 预约ID（支付需要）

	// ==================== 医生信息 ====================
	private String doctorName;          // 医生姓名：如 "张医生"
	private String title;               // 医生职称：如 "主任医师" / "副主任医师"
	private String departmentName;      // 科室名称：如 "心血管内科"

	// ==================== 就诊信息 ====================
	private LocalDate scheduleDate;     // 就诊日期：数组格式 [2026, 4, 11]
	private String timeSlot;            // 就诊时段："上午" / "下午" / "晚上"
	private String patientName;         // 就诊人姓名：如 "苏晓贵"

	// ==================== 状态信息 ====================
	private Integer payStatus;          // 支付状态：0=待支付, 1=已支付
	private Integer orderStatus;        // 订单状态：1=待就诊, 2=已完成, 3=已取消
	private LocalDateTime payDeadline; // 支付截止时间：数组格式 [2026, 4, 10, 23, 59, 59]

	// ==================== 费用信息 ====================
	private BigDecimal fee;             // 挂号费用：如 new BigDecimal("50.00")

	// ==================== 时间信息 ====================
	private LocalDateTime createTime;       // 创建时间：数组格式 [2026, 4, 10, 22, 1, 51]
}
