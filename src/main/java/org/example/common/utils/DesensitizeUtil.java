package org.example.common.utils;

public class DesensitizeUtil {
	/**
	 * 姓名脱敏
	 */
	public static String name(String name) {
		if (name == null || name.length() <= 1) {
			return name;
		}
		return name.charAt(0) + "***";
	}

	/**
	 * 手机号脱敏
	 */
	public static String phone(String phone) {
		if (phone == null || phone.length() != 11) {
			return phone;
		}
		return phone.substring(0, 3) + "******" + phone.substring(9);
	}

	/**
	 * 身份证脱敏
	 */
	public static String idCard(String idCard) {
		if (idCard == null || idCard.length() < 10) {
			return idCard;
		}
		return idCard.substring(0, 6) + "**********" + idCard.substring(16);
	}

	/**
	 * 邮箱脱敏
	 */
	public static String email(String email) {
		if (email == null || !email.contains("@")) {
			return email;
		}
		String[] split = email.split("@");
		String name = split[0];
		if (name.length() <= 2) {
			return name + "***@" + split[1];
		}
		return name.substring(0, 2) + "***@" + split[1];
	}

	/**
	 * 地址脱敏
	 */
	public static String address(String address) {
		if (address == null || address.length() <= 6) {
			return address;
		}
		return address.substring(0, 6) + "***";
	}
}
