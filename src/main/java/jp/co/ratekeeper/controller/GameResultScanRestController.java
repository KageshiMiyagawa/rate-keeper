package jp.co.ratekeeper.controller;

import java.util.Arrays;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jp.co.ratekeeper.model.GameResultData;
import jp.co.ratekeeper.service.GameResultManageService;
import jp.co.ratekeeper.service.UserManageService;

@RestController
public class GameResultScanRestController {

	@Autowired
	private UserManageService userManageService;
	@Autowired
	private GameResultManageService gameResultManageService;
	
	@PostMapping(value = "/scan/game-result/{userId}")
	public ResponseEntity<?> postScanGameResult(@PathVariable String userId) {
		if (!userId.matches("[+-]?\\d*(\\.\\d+)?")) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("不正な会員IDが読み取りされました。");
		}
		if (CollectionUtils
				.isEmpty(userManageService.findUser(Arrays.asList(new Integer[] { Integer.valueOf(userId) })))) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("存在しない会員IDが読み取りされました。");
		}
		GameResultData gameResult = gameResultManageService.saveScanGameResult(userId);
		return ResponseEntity.ok(gameResult);
	}
	
}