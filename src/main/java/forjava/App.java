package forjava;

import java.util.Objects;
import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        DatabaseManager.initializeDatabase();
        
        URL url = Objects.requireNonNull(App.class.getResource("/forjava/AppView.fxml"),
        "Missing /forjava/AppView.fxml in classpath");

        Parent root = FXMLLoader.load(url);

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("OCR For Java");
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
