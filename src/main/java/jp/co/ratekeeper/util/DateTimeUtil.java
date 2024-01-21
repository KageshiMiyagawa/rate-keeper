package jp.co.ratekeeper.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.co.ratekeeper.ApplicationConstants;

public class DateTimeUtil {

	private static final List<String> DATE_INPUT_FORMATS = Arrays.asList(new String[] {
			ApplicationConstants.DATETIME_FORMAT_FOR_INPUT, ApplicationConstants.DATETIME_FORMAT_FOR_INPUT_2 });

	public static String getNowDateStr(String format) {
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return now.format(formatter);
	}

	public static String convertDateStrFormat(String inputDateStr, String inputFormat, String outputFormat) {
		String convertDataStr;
		SimpleDateFormat inputDateFormat = new SimpleDateFormat(inputFormat);
		SimpleDateFormat outputDateFormat = new SimpleDateFormat(outputFormat);
		try {
			Date inputDate = inputDateFormat.parse(inputDateStr);
			convertDataStr = outputDateFormat.format(inputDate);
			return convertDataStr;
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// 想定外のフォーマットが指定された場合、システムが予想するフォーマットで変換を実施する。
		for (String format : DATE_INPUT_FORMATS) {
			try {
				inputDateFormat = new SimpleDateFormat(format);
				Date inputDate = inputDateFormat.parse(inputDateStr);
				convertDataStr = outputDateFormat.format(inputDate);
				return convertDataStr;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		throw new RuntimeException("想定外の形式が指定されました。形式：" + inputFormat + ", 値:" + inputDateStr);
	}
}
