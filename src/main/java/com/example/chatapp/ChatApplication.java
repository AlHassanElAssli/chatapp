package com.example.chatapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChatApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Create an instance of FXMLLoader and specify the FXML file to load
        FXMLLoader fxmlLoader = new FXMLLoader(ChatApplication.class.getResource("login.fxml"));

        // Load the FXML file and create a scene
        Scene scene = new Scene(fxmlLoader.load());

        // Set the title of the stage
        stage.setTitle("ChatApp");

        // Set the application icon by loading an image file
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("loginIcon.png")).toExternalForm()));

        // Set the scene on the stage
        stage.setScene(scene);

        // Show the stage
        stage.show();
    }

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch();
    }
}
