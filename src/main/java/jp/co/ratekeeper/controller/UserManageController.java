package jp.co.ratekeeper.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.co.ratekeeper.code.SortType;
import jp.co.ratekeeper.code.UserType;
import jp.co.ratekeeper.model.UserData;
import jp.co.ratekeeper.model.UserFetchCond;
import jp.co.ratekeeper.model.table.TtUser;
import jp.co.ratekeeper.service.UserManageService;
import jp.co.ratekeeper.util.DispMessageUtil;

@Controller
public class UserManageController {

	@Autowired
	private UserManageService userManageService;

	@GetMapping(value = "/user-manage")
	public String dispUserManage(Model model) {
		dispOptions(model, null);
		model.addAttribute("message", "メニューから操作を選択してください");
		return "userManage";
	}
	
	@GetMapping(value = "/user/{userId}")
	public String dispUserDetail(@PathVariable String userId, Model model) throws Exception {
		UserData userData = userManageService.findUser(Arrays.asList(new Integer[] { Integer.valueOf(userId) })).get(0);
		model.addAttribute("message", "会員ID:" + userData.getUserId() + " 会員名:" + userData.getUserName() + " を編集します。");
		model.addAttribute("userData", userData);
		return "userManageDetail";
	}
	
	@GetMapping(value = "/user")
	public String getUser(@ModelAttribute UserFetchCond userFetchCond, Model model) throws Exception {
		dispOptions(model, null);
		if (StringUtils.isNotEmpty(userFetchCond.getSortItem()) && StringUtils.isEmpty(userFetchCond.getSortType()) ||
				StringUtils.isNotEmpty(userFetchCond.getSortType()) && StringUtils.isEmpty(userFetchCond.getSortItem())) {
			model.addAttribute("errorMessage", "ソートを行う場合はソート項目とソート種別を両方指定してください。");
			return "userManage";
		}
		List<UserData> userDataList = userManageService.find(userFetchCond);
		if (CollectionUtils.isEmpty(userDataList)) {
			model.addAttribute("message", "該当する会員情報が登録されていません。");
		} else {
			model.addAttribute("message", "会員情報を" + userDataList.size() + "件取得しました!");
			model.addAttribute("userDataList", userDataList);
		}
		
		// 検索条件を画面表示
		Map<String, String> fetchCondMap = new LinkedHashMap<>();
		fetchCondMap.put("会員名", userFetchCond.getUserName());
		fetchCondMap.put("会員種別", StringUtils.isEmpty(userFetchCond.getUserType()) ? null
				: UserType.getUserTypeByCode(userFetchCond.getUserType()).getName());
		fetchCondMap.put("入会日（開始）", userFetchCond.getJoinStartDate());
		fetchCondMap.put("入会日（終了）", userFetchCond.getJoinEndDate());
		fetchCondMap.put("ソート項目", userFetchCond.getSortItem());
		fetchCondMap.put("ソート種別", StringUtils.isEmpty(userFetchCond.getSortType()) ? null
				: SortType.getSortTypeByCode(userFetchCond.getSortType()).getName());
		
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
		
		return "userManage";
	}
	
	@PostMapping(value = "/user")
	public String postUser(@ModelAttribute @Valid UserData userData, BindingResult bindingResult, Model model) {
		dispOptions(model, "regist_form");
		if (bindingResult.hasErrors()) {
			model.addAttribute("errorMessage", DispMessageUtil.createDispErrorMessage(bindingResult));
			return "userManage";
		}
		UserData registUserData = userManageService.save(userData);
		model.addAttribute("message", "会員情報を登録しました!");
		model.addAttribute("registUserData", registUserData);
		model.addAttribute("dispInfoFormFlg", 1);
		return "userManage";
	}
	
    @PostMapping("/user-upload")
    public String postCsvFile(@RequestParam("file") MultipartFile file, Model model) throws Exception {
    	dispOptions(model, "upload_form");
    	if (StringUtils.isEmpty(file.getOriginalFilename())) {
    		model.addAttribute("errorMessage", "CSV読込に失敗しました。ファイル未指定です。");
    		return "userManage";
    	}
    	try {
    		List<TtUser> gameList = userManageService.saveCsvFileData(file);
    		model.addAttribute("message", "会員情報を" + gameList.size() + "件登録しました!");
    	} catch (Exception e) {
    		model.addAttribute("errorMessage", "CSV読込に失敗しました。ファイルの内容が不正です。");
    	}
        return "userManage";
    }

	@PostMapping(value = "/user/{userId}")
	public String putUserDetail(@ModelAttribute UserData userData, Model model) throws Exception {
		UserData updateUserData = userManageService.save(userData);
		model.addAttribute("message", "会員情報を更新しました！");
		model.addAttribute("userData", updateUserData);
		return "userManageDetail";
	}
    
    private void dispOptions(Model model, String selectedFormName) {
    	// フォーム情報の設定
		if (StringUtils.isNotEmpty(selectedFormName)) {
			model.addAttribute("selectedFormName", selectedFormName);
		}
    }   
}