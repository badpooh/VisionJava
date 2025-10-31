package forjava;

import java.util.Objects;
import java.net.URL;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private AppController controller;

    @Override
    public void start(Stage stage) throws Exception {

        DatabaseManager.initializeDatabase();

        URL url = Objects.requireNonNull(App.class.getResource("/forjava/AppView.fxml"),
                "Missing /forjava/AppView.fxml in classpath");

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        controller = loader.getController();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (controller != null)
                controller.stopServerOnExit();
        }, "shutdown-hook-stop-python"));

        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle("OCR For Java");

        stage.setOnCloseRequest(ignored -> {
            if (controller != null)
                controller.stopServerOnExit();
            Platform.exit();
        });

        stage.setOnHidden(e -> {
            if (controller != null)
                controller.stopServerOnExit();
        });

        stage.show();
    }

    @Override
    public void stop() {
        if (controller != null)
            controller.stopServerOnExit();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
