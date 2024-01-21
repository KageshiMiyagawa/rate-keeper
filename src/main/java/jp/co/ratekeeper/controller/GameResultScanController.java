package jp.co.ratekeeper.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jp.co.ratekeeper.service.GameResultManageService;

/**
 * QRコードからスキャンしたURLからThymeleafのテンプレートに紐づけするコントローラー<br>
 * RestControllerではThymeleafとの紐づけができないため、別クラスにて作成する。<br>
 */
@Controller
public class GameResultScanController {

	@Autowired
	private GameResultManageService gameResultManageService;
	
	@GetMapping(value = "/scan/game-result")
	public String dispGameResultScan() {
		return "gameResultScan";
	}
	
	@GetMapping(value = "/scan/game-result/{userId}")
	public String scanGameResult(@PathVariable String userId, Model model) {
		model.addAttribute("optionalMethod", "scan");
		model.addAttribute("optionalValue", userId);
		return "gameResultScan";
	}
	
	@DeleteMapping(value = "/scan/game-result/{gameId}") 
	public String deleteScanGameResult(@PathVariable String gameId, Model model) {
		gameResultManageService.deleteGameResult(Integer.valueOf(gameId));
		model.addAttribute("message", "スキャンした対局結果を削除しました。対局ID:" + gameId);
		return "gameResultScan";
	}
	
}