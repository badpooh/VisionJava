package forjava;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FastApiClient {

	private static final String JSON_KEY_IMAGE_PATH = "image_path";
	private static final String JSON_KEY_ROI_KEYS = "roi_keys";

	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public FastApiClient() {
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = new ObjectMapper();
	}

	public String sendConnectRequest(String ip, int setupPort, int touchPort) throws IOException, InterruptedException {
		Map<String, Object> requestData = new HashMap<>();
		requestData.put(Config.JSON_KEY_IP, ip);
		requestData.put(Config.JSON_KEY_SETUP_PORT, setupPort);
		requestData.put(Config.JSON_KEY_TOUCH_PORT, touchPort);

		String jsonPayload = objectMapper.writeValueAsString(requestData);
		HttpRequest request = buildPostRequest(Config.SERVER_CONNECT_ENDPOINT, jsonPayload);

		return sendRequest(request);
	}

	public String sendDisconnectRequest() throws IOException, InterruptedException {
		HttpRequest request = buildPostRequest(Config.SERVER_DISCONNECT_ENDPOINT, HttpRequest.BodyPublishers.noBody());
		return sendRequest(request);
	}

	public String sendStopTestRequest() throws IOException, InterruptedException {
		HttpRequest request = buildPostRequest(Config.STOP_TEST, HttpRequest.BodyPublishers.noBody());
		return sendRequest(request); // 공통 전송 로직 사용
	}

	public String sendInitializationRequest() throws IOException, InterruptedException {
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("message", "Java client requested initialization");
		String jsonPayload = objectMapper.writeValueAsString(requestData);

		HttpRequest request = buildPostRequest(Config.INITIALIZE_ENDPOINT, jsonPayload);
		return sendRequest(request);
	}

	public String sendTestModeBalance() throws IOException, InterruptedException {
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("message", "Java client requested test mode balance");
		String jsonPayload = objectMapper.writeValueAsString(requestData);

		HttpRequest request = buildPostRequest(Config.TEST_MODE_BALANCE_ENDPOINT, jsonPayload);
		return sendRequest(request);
	}

	public String sendOcrRequest(TestCase testCase) throws IOException, InterruptedException {
		// 임시 데이터 (실제로는 testCase에서 값을 가져와야 함)
		String imagePath = "D:/path/to/your/image.png";
		List<String> roiKeys = List.of("key1", "key2");

		Map<String, Object> requestData = new HashMap<>();
		requestData.put(JSON_KEY_IMAGE_PATH, imagePath);
		requestData.put(JSON_KEY_ROI_KEYS, roiKeys);

		String jsonPayload = objectMapper.writeValueAsString(requestData);
		HttpRequest request = buildPostRequest(Config.OCR_ENDPOINT, jsonPayload);

		return sendRequest(request);
	}

	// --- 헬퍼 메소드 ---

	/**
	 * POST 요청 객체를 생성합니다. (JSON 페이로드)
	 */
	private HttpRequest buildPostRequest(String url, String jsonPayload) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
				.build();
	}

	/**
	 * POST 요청 객체를 생성합니다. (Body 없음)
	 */
	private HttpRequest buildPostRequest(String url, HttpRequest.BodyPublisher bodyPublisher) {
		return HttpRequest.newBuilder()
				.uri(URI.create(url))
				.POST(bodyPublisher)
				.build();
	}

	/**
	 * 공통 HTTP 요청 전송 및 응답 처리 로직
	 */
	private String sendRequest(HttpRequest request) throws IOException, InterruptedException {
		try {
			HttpResponse<String> response = httpClient.send(request,
					HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
			System.out.println("[HTTP Response] Status: " + response.statusCode() + ", Body: " + response.body());

			if (response.statusCode() == 200) {
				return response.body(); // 성공 시 JSON 응답 본문 반환
			} else {
				// 서버 에러 응답을 예외로 던지거나, 에러 메시지 반환
				throw new IOException(
						"Server Error: " + response.statusCode() + " - " + parseErrorFromJson(response.body()));
			}
		} catch (ConnectException e) {
			System.err.println("[HTTP Error] Failed to connect to Python server: " + e.getMessage());
			throw new IOException("Python 서버에 연결할 수 없습니다. 서버가 실행 중인지 확인하세요.", e);
		} catch (IOException | InterruptedException e) {
			System.err.println("[HTTP Error] Request failed: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * JSON 에러 응답을 파싱합니다.
	 */
	public String parseErrorFromJson(String jsonErrorResponse) {
		try {
			Map<String, Object> errorMap = objectMapper.readValue(jsonErrorResponse, Map.class);
			if (errorMap.containsKey("detail")) {
				return errorMap.get("detail").toString();
			}
		} catch (IOException e) {
			/* 파싱 실패 시 원본 반환 */ }
		return jsonErrorResponse;
	}

	/**
	 * JSON 성공 응답을 파싱합니다.
	 */
	public String parseResultFromJson(String jsonResponse) {
		try {
			Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
			if ("success".equals(responseMap.get("status")) && responseMap.containsKey("results")) {
				Object results = responseMap.get("results");
				return (results != null) ? results.toString() : "Success (no results)";
			} else if (responseMap.containsKey("detail")) {
				return "Error: " + responseMap.get("detail").toString();
			} else {
				return "Unknown response";
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "Response Parsing Error";
		}
	}
}