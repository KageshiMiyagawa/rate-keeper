package jp.co.ratekeeper.model;

import lombok.Data;

@Data
public class GameResultFetchCond {

	private String startDate;
	
	private String endDate;

	private String winnerId;

	private String loserId;
	
	private String syncFlg;
	
	private String sortType;
}
