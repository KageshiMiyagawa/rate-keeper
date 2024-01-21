package jp.co.ratekeeper.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalcResultData {
	
	private List<CalcResultUserData> calcResultUserData;
	
	private List<CalcResultGameData> calcResultGameData;
	
}