package jp.co.ratekeeper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jp.co.ratekeeper.model.CalcResultData;
import jp.co.ratekeeper.service.CalcRateService;

@Controller
public class CalcRateController {

	@Autowired
	private CalcRateService calcRateService;
	
	@GetMapping(value = "/calc-rate")
	public String dispCalcRate(Model model) {
		model.addAttribute("message", "メニューから操作を選択してください");
		return "calcRate";
	}
	
	@PostMapping(value = "/calc-rate")
	public String executeCalcRate(Model model) {
		try {
			CalcResultData calcResultData = calcRateService.calcRate();
			if (calcResultData.getCalcResultGameData().size() == 0) {
				model.addAttribute("message",  "レーティング計算対象がありませんでした。");
			} else {
				model.addAttribute("message",  "レーティング計算に成功しました！");
				model.addAttribute("calcResultUserDataList", calcResultData.getCalcResultUserData());
				model.addAttribute("calcResultGameDataList", calcResultData.getCalcResultGameData());
			}
		} catch (Exception e) {
			model.addAttribute("message", "レーティング計算に失敗しました。");
			e.printStackTrace();
		}
		return "calcRate";
	}
}