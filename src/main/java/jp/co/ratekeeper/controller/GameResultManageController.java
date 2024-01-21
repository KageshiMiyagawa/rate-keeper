package jp.co.ratekeeper.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.model.GameResultData;
import jp.co.ratekeeper.model.GameResultFetchCond;
import jp.co.ratekeeper.model.UserData;
import jp.co.ratekeeper.service.GameResultManageService;
import jp.co.ratekeeper.service.UserManageService;
import jp.co.ratekeeper.util.DateTimeUtil;
import jp.co.ratekeeper.util.DispMessageUtil;

@Controller
public class GameResultManageController {

	@Autowired
	private GameResultManageService gameResultManageService;
	@Autowired
	private UserManageService userManageService;

	@GetMapping(value = "/game-result-manage")
	public String dispGameResultManage(Model model) {
		model.addAttribute("message", "メニューから操作を選択してください");
		dispOptions(model, null);
		return "gameResultManage";
	}

	@GetMapping(value = "/game-result/{gameId}")
	public String dispGameResultDetail(@PathVariable String gameId, Model model) throws Exception {
		GameResultData gameResultData = gameResultManageService.find(Integer.valueOf(gameId));
		String gameDate = DateTimeUtil.convertDateStrFormat(gameResultData.getGameDate(), ApplicationConstants.DATETIME_FORMAT, ApplicationConstants.DATETIME_FORMAT_FOR_DISP);
		model.addAttribute("message", "対局ID:" + gameResultData.getGameId() + " 対局日時:" + gameDate + " を編集します。");
		model.addAttribute("gameResult", gameResultData);
		dispOptions(model, null);
		return "gameResultManageDetail";
	}
	
	@GetMapping(value = "/game-result")
	public String getGameResult(@ModelAttribute GameResultFetchCond gameFetchCond, Model model) throws Exception {
		List<GameResultData> gameResultList = gameResultManageService.find(gameFetchCond);
		if (CollectionUtils.isEmpty(gameResultList)) {
			model.addAttribute("message", "該当する対局結果が登録されていません。");
		} else {
			model.addAttribute("message", "対局結果を" + gameResultList.size() + "件取得しました!");
			model.addAttribute("gameResultList", gameResultList);
		}
		
		// 検索条件を画面表示
		List<UserData> userDataList = userManageService.findUser(null);
		Map<Integer, String> userMap = userDataList.stream()
				.collect(Collectors.toMap(UserData::getUserId, UserData::getUserName));
		Map<String, String> fetchCondMap = new LinkedHashMap<>();
		fetchCondMap.put("勝者", StringUtils.isEmpty(gameFetchCond.getWinnerId()) ? null
				: userMap.get(Integer.valueOf(gameFetchCond.getWinnerId())));
		fetchCondMap.put("敗者", StringUtils.isEmpty(gameFetchCond.getLoserId()) ? null
				: userMap.get(Integer.valueOf(gameFetchCond.getLoserId())));
		fetchCondMap.put("対局日時（開始）", gameFetchCond.getStartDate());
		fetchCondMap.put("対局日時（終了）", gameFetchCond.getEndDate());
		
		StringJoiner join = new StringJoiner(", ");
		for (Map.Entry<String, String> entry : fetchCondMap.entrySet()) {
		    if (StringUtils.isNotEmpty(entry.getValue())) {
		    	join.add(entry.getKey() + ":" + entry.getValue());
		    }
		}
		if (join.length() == 0) {
			join.add("指定なし");
		}
		model.addAttribute("infoMessage", "【検索条件】" + join.toString());
		
		dispOptions(model, "search_form");
		return "gameResultManage";
	}

	@PostMapping(value = "/game-result")
	public String postGameResult(@ModelAttribute @Valid GameResultData gameData, BindingResult bindingResult, Model model) {
		dispOptions(model, "regist_form");
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", DispMessageUtil.createDispErrorMessage(bindingResult));
			return "gameResultManage";
		}
		GameResultData registGameData = gameResultManageService.save(gameData);
		model.addAttribute("message", "対局結果を登録しました!");
		model.addAttribute("registGameData", registGameData);
		model.addAttribute("dispInfoFormFlg", 1);
		return "gameResultManage";
	}
	
    @PostMapping("/game-result-upload")
    @Validated
    public String postCsvFile(@RequestParam("file") MultipartFile file, Model model) throws Exception {
    	dispOptions(model, "upload_form");
    	if (StringUtils.isEmpty(file.getOriginalFilename())) {
    		model.addAttribute("errorMessage", "CSV読込に失敗しました。ファイル未指定です。");
    		return "gameResultManage";
    	}
    	try {
    		List<GameResultData> gameList = gameResultManageService.saveCsvFileData(file);
    		model.addAttribute("message", "対局結果を" + gameList.size() + "件登録しました!");
    	} catch (Exception e) {
    		e.printStackTrace();
    		model.addAttribute("errorMessage", "CSV読込に失敗しました。ファイルの内容が不正です。");
    	}
        return "gameResultManage";
    }
    
	@PostMapping(value = "/game-result/{userId}")
	public String updateGameResult(@ModelAttribute @Valid GameResultData gameResultData, Model model) {
		GameResultData savedGameResultData = gameResultManageService.save(gameResultData);
		model.addAttribute("message", "対局結果を更新しました!");
		model.addAttribute("warnMessage", "更新した対局以降のレート計算は初期化しているため、再計算が必要です。");
		model.addAttribute("gameResult", savedGameResultData);
		dispOptions(model, null);
		return "gameResultManageDetail";
	}
	
    private void dispOptions(Model model, String selectedFormName) {
    	// ユーザ情報の設定
		List<UserData> userDataList = userManageService.findUser(null);
		model.addAttribute("userDataList", userDataList);
		// フォーム情報の設定
		if (StringUtils.isNotEmpty(selectedFormName)) {
			model.addAttribute("selectedFormName", selectedFormName);
		}
    }
}