package jp.co.ratekeeper.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class GameResultData {

	private Integer gameId;

	@NotBlank(message = "対局日時が未指定です。")
	private String gameDate;
	
	@NotBlank(message = "勝者が未指定です。")	
	private String winner;
	
	@NotBlank(message = "敗者が未指定です。")	
	private String loser;

	private String syncFlg;
	
	private Integer floatingRate;
	
	private String updateDate;
}
