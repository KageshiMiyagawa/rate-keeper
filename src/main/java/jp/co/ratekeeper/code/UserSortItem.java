package jp.co.ratekeeper.code;

import org.apache.commons.lang3.StringUtils;

public enum UserSortItem {

	USER_ID("userId", "会員ID"),
	USER_NAME("userName","会員名"),
	GRADE("grade","段位"),
	RATE("rate","レーティング"),
	JOIN_DATE("joinDate","入会日");
	
	UserSortItem(String code, String name) {
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
	
	public static UserSortItem getUserSortItemByCode (String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		for (UserSortItem grade : UserSortItem.values()) {
			if (StringUtils.equals(code, grade.getCode())) {
				return grade;
			}
		}
		return null;
	}
	
	public static UserSortItem getUserSortItemByName (String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (UserSortItem grade : UserSortItem.values()) {
			if (StringUtils.equals(name, grade.getName())) {
				return grade;
			}
		}
		return null;
	}
}
