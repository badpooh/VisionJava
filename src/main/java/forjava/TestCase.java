package forjava;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TestCase {

    private final SimpleIntegerProperty num;
    private final SimpleStringProperty title;
    private final SimpleStringProperty content;
    private final SimpleStringProperty result;

    private final SimpleBooleanProperty initialize;
    private final SimpleBooleanProperty balance;
    private final SimpleBooleanProperty unbalance;

    public TestCase(int num, String title, String content, String result) {
        this.num = new SimpleIntegerProperty(num);
        this.title = new SimpleStringProperty(title);
        this.content = new SimpleStringProperty(content);
        this.result = new SimpleStringProperty(result);

        this.initialize = new SimpleBooleanProperty(false);
        this.balance = new SimpleBooleanProperty(false);
        this.unbalance = new SimpleBooleanProperty(false);
    }

    public int getNum() { return num.get(); }
    public void setNum(int num) { this.num.set(num); }
    public String getTitle() { return title.get(); }
    public void setTitle(String title) { this.title.set(title); }
    public String getContent() { return content.get(); }
    public void setContent(String content) { this.content.set(content); }
    public String getResult() { return result.get(); }
    public void setResult(String result) { this.result.set(result); }
    public boolean isInitialize() { return initialize.get(); }
    public void setInitialize(boolean initialize) { this.initialize.set(initialize); }
    public boolean isBalance() { return balance.get(); }
    public void setBalance(boolean balance) { this.balance.set(balance); }
    public boolean isUnbalance() { return unbalance.get(); }
    public void setUnbalance(boolean unbalance) { this.unbalance.set(unbalance); }

    // --- TableView가 변경을 감지하기 위해 필요한 Property Getter 메소드 (핵심!) ---
    public IntegerProperty numProperty() {
        return num;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public StringProperty contentProperty() {
        return content;
    }

    public StringProperty resultProperty() {
        return result;
    }

    public BooleanProperty initializeProperty() {
        return initialize;
    }

    public BooleanProperty balanceProperty() {
        return balance;
    }

    public BooleanProperty unbalanceProperty() {
        return unbalance;
    }
}