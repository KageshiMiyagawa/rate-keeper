package jp.co.ratekeeper;

public class ApplicationConstants {
	
	public static final String DATETIME_FORMAT = "yyyyMMddHHmmss";
	public static final String DATETIME_FORMAT_FOR_INPUT = "yyyy-MM-dd'T'HH:mm";
	public static final String DATETIME_FORMAT_FOR_INPUT_2 = "yyyy-MM-dd HH:mm";
	public static final String DATETIME_FORMAT_FOR_DISP = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	
	public static final int DEFAULT_RATE = 1500;
	// TODO HTTP公開する場合はhost名に変更する
	public static final String QR_IMAGE_URL = "http://localhost:8080/scan/game-result";
}
