package jp.co.ratekeeper.code;

import org.apache.commons.lang3.StringUtils;

public enum SortType {

	ASC("1","昇順"),
	DESC("2","降順");
	
	SortType(String code, String name) {
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
	
	public static SortType getSortTypeByCode (String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		for (SortType grade : SortType.values()) {
			if (StringUtils.equals(code, grade.getCode())) {
				return grade;
			}
		}
		return null;
	}
	
	public static SortType getSortTypeByName (String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (SortType grade : SortType.values()) {
			if (StringUtils.equals(name, grade.getName())) {
				return grade;
			}
		}
		return null;
	}
}
