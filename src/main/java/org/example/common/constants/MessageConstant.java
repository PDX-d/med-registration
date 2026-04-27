package org.example.common.constants;

/**
 * 消息提示常量类
 */
public class MessageConstant {
	/**
	 * 登录认证相关
	 */
	public static final String LOGIN_ERROR = "用户名或密码错误";
	public static final String ERR_USER_NOT_LOGIN = "用户未登录";
	public static final String LOGIN_CODE_ERROR = "验证码错误";
	public static final String USER_DISABLED = "用户被禁用";
	public static final String TOKEN_EXPIRED = "JWT 令牌解析失败(accessToken过期)";
	public static final String TOKEN_INVALID = "JWT 令牌解析失败(accessToken签名错误,篡改)";
	public static final String TOKEN_PARSE_ERROR = "JWT 令牌解析失败";
	public static final String REFRESH_TOKEN_EXPIRED_ERROR = "登录已过期,请重新登录";
	public static final String GET_OPENID_ERROR = "登录校验失败,请重新登录";
	public static final String PASSWORD_MODIFY_ERROR = "密码修改失败,请重试";
	public static final String USER_NOT_LOGIN = "用户登录异常,请重新登录";
	public static final String WECHAT_CODE_EMPTY = "微信登录失败,请重试";
	public static final String PHONE_INVALID = "手机号格式错误";
	public static final String PHONE_NOT_NULL = "手机号不能为空";
	public static final String USER_EXISTS = "该账号已经注册";

	/**
	 * 权限与账号状态
	 */
	public static final String ACCOUNT_LOCKED = "账号被锁定";
	public static final String PERMISSION_DENIED = "权限不足";
	public static final String ACCOUNT_NOT_FOUND = "账号不存在";
	public static final String USER_NAME_EXISTS = "用户名已存在";
	public static final String USER_NAME_NOT_NULL = "用户名不能为空";

	/**
	 * 系统与网络错误
	 */
	public static final String SYSTEM_ERROR = "服务器异常";
	public static final String ERR_SERVER_ERROR = "系统繁忙，请稍后重试";
	public static final String NETWORK_ERROR = "网络异常";
	public static final String TOM_CAT_ERROR = "系统繁忙，请稍后重试";
	public static final String UNKNOWN_ERROR = "未知错误";

	/**
	 * 数据与参数错误
	 */
	public static final String INPUT_DATA_ERROR = "输入数据不合法";
	public static final String ERR_PARAM_ERROR = "参数错误";
	public static final String DATA_ERROR = "数据异常,请重试";
	public static final String SQL_MESSAGE_SAVE_ERROR = "数据保存失败，请稍后重试";
	public static final String JSON_CONVERT_ERROR = "JSON 序列化异常";
	public static final String DATE_TIME_PARSE_ERROR = "时间格式转换异常";

	/**
	 * 业务操作错误
	 */
	public static final String ORDER_NOT_FOUND = "订单不存在";
	public static final String CART_NOT_EXIST_ERROR = "删除的购物车不存在";
	public static final String CONTENT_NOT_EXIST_ERROR = "删除的内容不存在";
	public static final String DELETE_ERROR = "删除失败,请重试";
	public static final String DEPT_ADD_ERROR = "科室添加失败";
	public static final String ANNO_ADD_ERROR = "公告添加失败";
	public static final String ANNO_NOT_FOUND = "公告不存在";
	public static final String UPDATE_ERROR = "更新失败";
	public static final String DEPT_NOT_FOUND = "部门不存在";

	/**
	 * 系统配置常量
	 */
	public static final String DEFAULT_PASSWORD = "123456"; // 默认密码
	public static final Integer STATUS_ENABLE = 1; // 启用
	public static final Integer STATUS_DISABLE = 0; // 停用

	//图片上传失败
	public static final String UPLOAD_ERROR = "图片上传失败";
}
