package jp.co.ratekeeper.model;

import lombok.Data;

@Data
public class CalcResultGameData {
	
	private Integer gameId;
	
	private String gameDate;

	private String winner;

	private Integer winnerRateBefore;
	
	private Integer winnerRateAfter;

	private String loser;

	private Integer loserRateBefore;
	
	private Integer loserRateAfter;

}