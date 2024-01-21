package jp.co.ratekeeper.code;

import org.apache.commons.lang3.StringUtils;

public enum UserType {

	GENERAL("1","一般会員"),
	ADMINISTRATOR("2","管理者");
	
	UserType(String code, String name) {
		this.code = code;
		this.name = name;
	}
	
	private String code;
	private String name;
	
	public String getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	
	public static UserType getUserTypeByName (String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (UserType userType : UserType.values()) {
			if (StringUtils.equals(name, userType.getName())) {
				return userType;
			}
		}
		return null;
	}
	
	public static UserType getUserTypeByCode (String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		for (UserType userType : UserType.values()) {
			if (StringUtils.equals(code, userType.getCode())) {
				return userType;
			}
		}
		return null;		
	}
}
