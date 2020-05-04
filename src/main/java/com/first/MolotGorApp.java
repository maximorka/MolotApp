package com.first;

import com.first.ui.MainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MolotGorApp extends Application {
    private static Stage stage;

    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        show();
    }

    private static void show() {
        Platform.runLater(() -> {
            try {

                Parent page = FXMLLoader.load(MainController.class.getResource("/fxml/Main.fxml"));
                Scene scene = new Scene(page);
                scene.getStylesheets().add(MolotGorApp.class.getResource("/css/Main.css").toExternalForm());
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
                stage.setOnCloseRequest(event -> {
                    System.out.println("platform exit");
                    Platform.exit();
                    System.exit(0);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
