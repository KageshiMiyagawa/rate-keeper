package jp.co.ratekeeper.controller;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.zxing.WriterException;

import jp.co.ratekeeper.ApplicationConstants;
import jp.co.ratekeeper.model.UserData;
import jp.co.ratekeeper.service.QrCodeService;
import jp.co.ratekeeper.service.UserManageService;

@Controller
public class QrCodeController {

	@Autowired
	private QrCodeService qrCodeService;
	@Autowired
	private UserManageService userManageService;
	
	@GetMapping(value = "/qrCode")
	public String dispQrCode(Model model) {
		model.addAttribute("message", "メニューから操作を選択してください");
		dispUserOptions(model);
		return "qrCode";
	}

	@GetMapping(value = "/qrCode-generate")
	public String generateQrCode(@ModelAttribute("userId") String userId,
			BindingResult bindingResult, Model model) throws Exception {
		dispUserOptions(model);
		if (StringUtils.isEmpty(userId)) {
			model.addAttribute("errorMessage", "会員が未指定です。");
			return "qrCode";
		}
		model.addAttribute("userId", userId);
		model.addAttribute("message", "会員ID:" + userId + " のQRコードを生成しました!");
		return "qrCode";
	}
	
    @GetMapping("/imageBytes/{userId}")
    @ResponseBody
    public byte[] getImage(@PathVariable String userId) throws IOException, WriterException {
    	String imageUrl = ApplicationConstants.QR_IMAGE_URL + userId;
        return qrCodeService.generateQrCode(imageUrl, 256, 256);
    }
	
    private void dispUserOptions(Model model) {
		List<UserData> userDataList = userManageService.findUser(null);
		model.addAttribute("userDataList", userDataList);
    }
    
}