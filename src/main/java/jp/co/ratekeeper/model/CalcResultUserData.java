package jp.co.ratekeeper.model;

import lombok.Data;

@Data
public class CalcResultUserData {
	
	private Integer userId;
	
	private String userName;

	private Integer beforeRate;

	private Integer afterRate;
	
	private String riseAndFall;

}