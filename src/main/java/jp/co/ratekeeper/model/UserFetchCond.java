package jp.co.ratekeeper.model;

import lombok.Data;

@Data
public class UserFetchCond {

	private String userType;

	private String userName;

	private String grade;

	private String joinStartDate;
	
	private String joinEndDate;
	
	private String sortItem;
	
	private String sortType;
}
