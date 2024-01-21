package jp.co.ratekeeper.code;

import org.apache.commons.lang3.StringUtils;

public enum Grade {

	DAN_NINE("+9","九段"),
	DAN_EIGHT("+8","八段"),
	DAN_SEVEN("+7","七段"),
	DAN_SIX("+6","六段"),
	DAN_FIVE("+5","五段"),
	DAN_FOUR("+4","四段"),
	DAN_THREE("+3","三段"),
	DAN_TWO("+2","二段"),
	DAN_ONE("+1","初段"),
	KYU_ONE("-1","初級"),
	KYU_TWO("-2","二級"),
	KYU_THREE("-3","三級"),
	KYU_FOUR("-4","四級"),
	KYU_FIVE("-5","五級"),
	KYU_SIX("-6","六級"),
	KYU_SEVEN("-7","七級"),
	KYU_EIGHT("-8","八級"),
	KYU_NINE("-9","九級");
	
	Grade(String code, String name) {
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
	
	public static Grade getGradeByCode (String code) {
		if (StringUtils.isEmpty(code)) {
			return null;
		}
		for (Grade grade : Grade.values()) {
			if (StringUtils.equals(code, grade.getCode())) {
				return grade;
			}
		}
		return null;
	}
	
	public static Grade getGradeByName (String name) {
		if (StringUtils.isEmpty(name)) {
			return null;
		}
		for (Grade grade : Grade.values()) {
			if (StringUtils.equals(name, grade.getName())) {
				return grade;
			}
		}
		return null;
	}
}
