package test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestJavaFX extends Application {

    @Override
    public void start(Stage stage) {

        Label label = new Label("JavaFX fonctionne correctement ✅");
        label.setStyle("-fx-font-size: 18px; -fx-text-fill: green;");

        StackPane root = new StackPane(label);
        Scene scene = new Scene(root, 350, 180);

        stage.setTitle("Test JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
