package forjava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.File;
import java.net.URISyntaxException;
import java.security.CodeSource;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AppController implements Initializable {

    @FXML private TextField tcpIpField;
    @FXML private TextField setupPortField;
    @FXML private TextField touchPortField;
    @FXML private TableView<TestCase> tableView;
    @FXML private TableColumn<TestCase, Integer> colNum;
    @FXML private TableColumn<TestCase, String> colTitle;
    @FXML private TableColumn<TestCase, String> colContent;
    @FXML private TableColumn<TestCase, String> colResult;

    @FXML private Button settingButton;
    @FXML private Button connectButton;
    @FXML private Button disconnectButton;
    @FXML private Button startButton;
    @FXML private Button stopButton;

    private final DatabaseManager dbManager = new DatabaseManager();
    private static final String KEY_TCP_IP = "LAST_TCP_IP";
    private static final String KEY_SETUP_PORT = "LAST_SETUP_PORT";
    private static final String KEY_TOUCH_PORT = "LAST_TOUCH_PORT";

    private ObservableList<TestCase> testCaseList = FXCollections.observableArrayList();
    private Task<Void> testRunnerTask; // 현재 실행 중인 테스트 작업을 저장할 변수

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadLastSettings();
        setupTableView();
    }

    // --- (기존의 다른 메소드들은 여기에 그대로 유지됩니다) ---
    private void loadLastSettings() {
        String lastIp = dbManager.loadSetting(KEY_TCP_IP);
        if (lastIp != null) {
            tcpIpField.setText(lastIp);
        }

        String lastSetupPort = dbManager.loadSetting(KEY_SETUP_PORT);
        if (lastSetupPort != null) {
            setupPortField.setText(lastSetupPort);
        }

        String lastTouchPort = dbManager.loadSetting(KEY_TOUCH_PORT);
        if (lastTouchPort != null) {
            touchPortField.setText(lastTouchPort);
        }
    }
    
    @FXML
    private void handleTcpIpEnter() {
        String ip = tcpIpField.getText().trim();
        if (!ip.isEmpty()){
            dbManager.saveOrUpdateSetting(KEY_TCP_IP, ip);
            System.out.println("Saved TCP/IP: " + ip);
        } else {
            System.out.println("TCP/IP field is empty");
        }
    }
    
    @FXML
    private void handleSetupPortEnter() {
        String setupPort = setupPortField.getText().trim();
        if (!setupPort.isEmpty()){
            dbManager.saveOrUpdateSetting(KEY_SETUP_PORT, setupPort);
            System.out.println("Saved Setup Port: " + setupPort);
        } else {
            System.out.println("Setup Port field is empty");
        }
    }
    
    @FXML
    private void handleTouchPortEnter() {
        String touchPort = touchPortField.getText().trim();
        if (!touchPort.isEmpty()){
            dbManager.saveOrUpdateSetting(KEY_TOUCH_PORT, touchPort);
            System.out.println("Saved Touch Port: " + touchPort);
        } else {
            System.out.println("Touch Port field is empty");
        }
    }

    @FXML
    private void handleAddTc() {
        int newNum = testCaseList.size() + 1;
        
        TestCase newTestCase = new TestCase(newNum, "New Title", "Options not set", "0/0");
        testCaseList.add(newTestCase);
        
        System.out.println("ADD TC button clicked. New row added.");
    }

    @FXML
    private void handleDelTc() {
        TestCase selectedItem = tableView.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            testCaseList.remove(selectedItem);
            System.out.println("DEL TC button clicked. Row " + selectedItem.getNum() + " removed.");
        } else {
            System.out.println("DEL TC button clicked, but no row was selected.");
        }
    }

    @FXML
    private void handleConnect() {
        System.out.println("Connect button clicked");
    }
    
    @FXML
    private void handleDisconnect() {
        System.out.println("Disconnect button clicked");
    }
    
    @FXML
    private void handleSetupPort() {
        System.out.println("Setup Port button clicked");
    }

    /**
     * 'START' 버튼을 누르면 실행됩니다.
     * UI가 멈추지 않도록 별도의 백그라운드 스레드에서 테스트를 시작합니다.
     */
    @FXML
    private void handleStart() {
        // 이미 테스트가 실행 중이면 중복 실행 방지
        if (testRunnerTask != null && testRunnerTask.isRunning()) {
            System.out.println("A test is already in progress.");
            return;
        }

        System.out.println("START button clicked. Starting background test task.");
        startButton.setDisable(true); // 시작 버튼 비활성화
        stopButton.setDisable(false); // 중지 버튼 활성화

        // 1. 백그라운드 작업을 정의하는 Task 객체를 생성합니다.
        testRunnerTask = new TestRunnerTask(testCaseList);

        // 2. 작업이 성공적으로 끝나면 실행될 코드를 정의합니다.
        testRunnerTask.setOnSucceeded(e -> {
            System.out.println("All tests finished successfully.");
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });

        // 3. 작업이 실패하면 실행될 코드를 정의합니다.
        testRunnerTask.setOnFailed(e -> {
            System.out.println("The test task failed.");
            testRunnerTask.getException().printStackTrace();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        });

        // 4. Task를 새로운 스레드에서 실행합니다.
        new Thread(testRunnerTask).start();
    }

    /**
     * 'STOP' 버튼을 누르면 실행됩니다.
     * 현재 실행 중인 백그라운드 테스트 작업을 취소합니다.
     */
    @FXML
    private void handleStop() {
        System.out.println("STOP button clicked");
        if (testRunnerTask != null) {
            testRunnerTask.cancel(); // 백그라운드 작업에 취소 신호를 보냅니다.
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleSave() {
        System.out.println("SAVE button clicked");
    }
    
    @FXML
    private void handleLoad() {
        System.out.println("LOAD button clicked");
    }

    private void setupTableView() {
        // 컬럼과 TestCase 필드 연결
        colNum.setCellValueFactory(new PropertyValueFactory<>("num"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colContent.setCellValueFactory(new PropertyValueFactory<>("content"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("result"));

        // TableView에 데이터 리스트 설정
        tableView.setItems(testCaseList);

        // TableView의 각 행(Row)에 더블클릭 이벤트 핸들러 추가
        tableView.setRowFactory(tv -> {
            TableRow<TestCase> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TestCase rowData = row.getItem();
                    openTestCaseSettingWindow(rowData);
                }
            });
            return row;
        });
    }

    private void openTestCaseSettingWindow(TestCase testCase) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forjava/TestCaseSetting.fxml"));
            Parent root = loader.load();

            TestCaseSettingController controller = loader.getController();
            controller.initData(testCase);

            Stage newStage = new Stage();
            newStage.setTitle("Test Case Settings for #" + testCase.getNum());
            newStage.setScene(new Scene(root));
            
            newStage.initOwner(tableView.getScene().getWindow());
            newStage.initModality(Modality.WINDOW_MODAL);
            
            newStage.showAndWait(); // 창이 닫힐 때까지 기다림
            tableView.refresh(); // 테이블 뷰를 새로고침하여 Content 변경사항을 즉시 반영

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Python 스크립트를 실행하고, 마지막 줄 출력을 반환합니다.
     * 이 메소드는 백그라운드 스레드에서 호출되어야 합니다.
     */
    private String executePythonScript(String content) throws IOException, InterruptedException, URISyntaxException {
        
        // CodeSource codeSource = App.class.getProtectionDomain().getCodeSource();
        // URL location = codeSource.getLocation();
        File appRoot;

        appRoot = new File(System.getProperty("user.dir"));

        String pythonExecutablePath = new File(appRoot, "python_env/python.exe").getAbsolutePath();
        String scriptPath = new File(appRoot, "python_scripts/main.py").getAbsolutePath();


        System.out.println("[PY] App Root: " + appRoot.getAbsolutePath());
        System.out.println("[PY] Executing: " + pythonExecutablePath + " " + scriptPath + " " + content);

        ProcessBuilder processBuilder = new ProcessBuilder(
            pythonExecutablePath,
            "-X", "utf8", // Python의 인코딩을 UTF-8로 강제
            scriptPath,
            content
        );
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
        String line;
        String lastLine = "No output";
        while ((line = reader.readLine()) != null) {
            System.out.println("Python: " + line);
            lastLine = line;
        }
        int exitCode = process.waitFor();
        System.out.println("[PY] exit code = " + exitCode);
        return lastLine;
    }

    /**
     * UI 스레드를 차단하지 않고 백그라운드에서 테스트를 실행하는 Task 클래스.
     */
    private class TestRunnerTask extends Task<Void> {
        private final ObservableList<TestCase> testsToRun;

        public TestRunnerTask(ObservableList<TestCase> testsToRun) {
            this.testsToRun = testsToRun;
        }

        @Override
        protected Void call() throws Exception {
            for (int i = 0; i < testsToRun.size(); i++) {
                // 'STOP' 버튼이 눌렸는지 매번 확인
                if (isCancelled()) {
                    System.out.println("Test execution cancelled by user.");
                    break;
                }

                TestCase currentTest = testsToRun.get(i);
                String content = currentTest.getContent();
                System.out.println("Executing test for: " + content);

                // UI 업데이트는 반드시 Platform.runLater를 통해 수행
                Platform.runLater(() -> currentTest.setResult("Running..."));

                try {
                    // Python 스크립트 실행 (이 작업은 백그라운드 스레드에서 실행됨)
                    String result = executePythonScript(content);

                    // 최종 결과를 UI에 업데이트
                    final String finalResult = result;
                    Platform.runLater(() -> currentTest.setResult(finalResult));

                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> currentTest.setResult("Error"));
                }
            }
            return null;
        }
    }
}
