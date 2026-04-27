package org.example.common.constants;

/**
 * 订单相关常量类
 */
public class OrderConstant {
	/** 订单状态 */
	public static final int ORDER_STATUS_SUCCESS = 1; // 预约成功
	public static final int ORDER_STATUS_CANCEL = 2; // 取消
	public static final int ORDER_STATUS_COMPLETED = 3; // 接诊

	/** 支付状态 */
	public static final int PAY_STATUS_UNPAID = 0; // 待支付
	public static final int PAY_STATUS_PAID = 1; // 已支付
	public static final int PAY_STATUS_FAILED = 2; // 退款

	/** 订单统一异常提示信息 */
	public static final String ERR_SCHEDULE_NOT_EXIST = "排班不存在";
	public static final String ERR_STOCK_NOT_ENOUGH = "库存不足";
	public static final String ERR_USER_EXIST = "用户已经购买过";
	public static final String ERR_APPOINT_REPEAT = "不可重复预约";
	public static final String ERR_ORDER_NOT_EXIST = "订单不存在";
	public static final String ERR_PAY_STATUS_ERROR = "支付状态异常";

}
