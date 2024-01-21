package jp.co.ratekeeper.util;

import java.util.StringJoiner;

import org.springframework.validation.BindingResult;

public class DispMessageUtil {

	public static String createDispErrorMessage(BindingResult bindingResult) {
		StringJoiner join = new StringJoiner(" ");
		bindingResult.getAllErrors().stream().forEach(err -> {
			join.add(err.getDefaultMessage());
		});
		
		return join.toString();
	}
	
}
