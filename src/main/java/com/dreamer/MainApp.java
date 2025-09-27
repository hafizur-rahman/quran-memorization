package com.dreamer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.text", "t2k");
        System.setProperty("prism.lcdtext", "false");

        Parent root = FXMLLoader.load(getClass().getResource("scene.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Quran Al-Karim");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("al-quran.jpg")));
        primaryStage.setScene(scene);

        // Handle window close request
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");

            // Perform cleanup if needed
            Platform.exit(); // Ensures the application exits properly

            System.exit(0);
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}