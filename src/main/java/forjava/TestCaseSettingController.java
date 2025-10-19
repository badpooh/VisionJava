package forjava;

import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TestCaseSettingController {

    @FXML private CheckBox initializeCheckbox;
    @FXML private CheckBox balanceCheckbox;
    @FXML private CheckBox unbalanceCheckbox;
    @FXML private Button applyButton;
    @FXML private Button cancelButton;

    private TestCase currentTestCase;

    public void initData(TestCase testCase){
        this.currentTestCase = testCase;
        initializeCheckbox.setSelected(testCase.isInitialize());
        balanceCheckbox.setSelected(testCase.isBalance());
        unbalanceCheckbox.setSelected(testCase.isUnbalance());
    }

    @FXML
    private void handleApply() {
        if (currentTestCase != null) {
            // 현재 체크박스 상태를 TestCase 객체에 저장
            currentTestCase.setInitialize(initializeCheckbox.isSelected());
            currentTestCase.setBalance(balanceCheckbox.isSelected());
            currentTestCase.setUnbalance(unbalanceCheckbox.isSelected());
            
            // 변경된 설정을 content 열에 시각적으로 표시 (예시)
            updateTestCaseContent();
        }
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void updateTestCaseContent() {
        // 1. 선택된 옵션의 이름만 담을 리스트를 생성합니다.
        List<String> selectedOptions = new ArrayList<>();

        // 2. 체크된 항목이 있으면, 리스트에 해당 텍스트를 추가합니다.
        if (currentTestCase.isInitialize()) {
            selectedOptions.add("Initialize");
        }
        if (currentTestCase.isBalance()) {
            selectedOptions.add("Balance");
        }
        if (currentTestCase.isUnbalance()) {
            selectedOptions.add("Unbalance");
        }

        // 3. String.join()을 사용해 리스트의 항목들을 ", "로 연결합니다.
        String newContent = String.join(", ", selectedOptions);

        // 4. 리스트가 비어있으면(newContent가 비어있으면) 기본 텍스트를, 아니면 연결된 텍스트를 설정합니다.
        if (newContent.isEmpty()) {
            currentTestCase.setContent("Options not set");
        } else {
            currentTestCase.setContent(newContent);
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) applyButton.getScene().getWindow();
        stage.close();
    }
}
