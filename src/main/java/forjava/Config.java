package forjava;

public final class Config {

	// 생성자를 private으로 만들어 인스턴스 생성 방지
	private Config() {
	}

	// --- Database Keys ---
	public static final String KEY_TCP_IP = "LAST_TCP_IP";
	public static final String KEY_SETUP_PORT = "LAST_SETUP_PORT";
	public static final String KEY_TOUCH_PORT = "LAST_TOUCH_PORT";

	// --- JSON Keys ---
	public static final String JSON_KEY_IP = "ip";
	public static final String JSON_KEY_SETUP_PORT = "setup_port";
	public static final String JSON_KEY_TOUCH_PORT = "touch_port";
	public static final String JSON_KEY_IMAGE_PATH = "image_path";
	public static final String JSON_KEY_ROI_KEYS = "roi_keys";

	// --- Server URLs ---
	public static final String SERVER_BASE_URL = "http://localhost:5000";
	public static final String SERVER_CONNECT_ENDPOINT = SERVER_BASE_URL + "/connect";
	public static final String SERVER_DISCONNECT_ENDPOINT = SERVER_BASE_URL + "/disconnect";
	public static final String SERVER_HEALTH_CHECK = SERVER_BASE_URL + "/health";
	public static final String SERVER_OCR_ENDPOINT = SERVER_BASE_URL + "/ocr";

	// --- TestCase Content ---
	public static final String INITIALIZE_ENDPOINT = SERVER_BASE_URL + "/Initialize";
	public static final String TEST_MODE_BALANCE_ENDPOINT = SERVER_BASE_URL + "/test_mode_balance";

	public static final String STOP_TEST = SERVER_BASE_URL + "/stop_test";

	public static final String OCR_ENDPOINT = SERVER_BASE_URL + "/ocr";

}
