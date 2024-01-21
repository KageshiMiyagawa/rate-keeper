package jp.co.ratekeeper.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationController {

	@GetMapping(value = "/")
	public String disp(Model model) throws Exception {
		return "redirect:/index";
	}

	@GetMapping(value = "/index")
	public String dispIndex(Model model) {
		model.addAttribute("message", "メニューから操作を選択してください");
		return "index";
	}

}