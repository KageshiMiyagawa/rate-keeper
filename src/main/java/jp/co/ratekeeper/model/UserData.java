package jp.co.ratekeeper.model;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class UserData {
	
	private Integer userId;
	
	@NotBlank(message = "会員種別が未指定です。")
	private String userType;

	@NotBlank(message = "会員名が未指定です。")
	private String userName;

	@NotBlank(message = "段位が未指定です。")
	private String grade;

	private Integer rate;
	
	@NotBlank(message = "入会日が未指定です。")
	private String joinDate;

}