package com.example.chatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;

public class LoginController {
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Button loginButton;
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Label errorLabel;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    @FXML
    private void initialize() {
        // Initialization code for the controller
        errorLabel.setVisible(false); // Hide the error label initially
    }

    @FXML
    private void handleLoginButton() throws IOException {
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText().trim();

        // Validate the username and password
        if (username.isEmpty() || password.isEmpty()) {
            displayError("Please enter both username and password.");
            return;
        }
        // Perform authentication or login logic here
        if (authenticateUser(username, password)) {
            // Successful login
            clearInputFields();
            errorLabel.setVisible(false);
            redirectToChatView();


        } else {
            // Failed login
            displayError("Invalid username or password.");
        }
    }
    private void redirectToChatView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent chatView = fxmlLoader.load();
            ChatController chatController = fxmlLoader.getController();
            chatController.setUsername(this.username);
            chatController.runClient();
            Stage stage = new Stage();
            stage.getIcons().add(new Image("C:\\Users\\hassa\\IdeaProjects\\ChatApp\\src\\main\\resources\\com\\example\\chatapp\\loginIcon.png"));
            stage.setTitle("Chat");
            stage.setScene(new Scene(chatView));
            stage.show();

            // Close the login view if needed
            Stage loginStage = (Stage) registerLink.getScene().getWindow();
            loginStage.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "root")) {
            // Query the database to check if the username and password match
            String query = "SELECT id FROM user WHERE username = ? AND password = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    // If there is a matching user in the database, set the username and return true
                    if (resultSet.next()) {
                        setUsername(username);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            // Handle any database errors
            e.printStackTrace();
        }

        return false; // Return false if there is an error or no matching user found
    }


    private void clearInputFields() {
        usernameInput.clear();
        passwordInput.clear();
    }

    private void displayError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleRegisterLink() {
        // Register link click handler
        loadSignupView();
    }

    private void loadSignupView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("signup.fxml"));
            Parent signupView = fxmlLoader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image("C:\\Users\\hassa\\IdeaProjects\\ChatApp\\src\\main\\resources\\com\\example\\chatapp\\loginIcon.png"));
            stage.setTitle("Sign Up");
            stage.setScene(new Scene(signupView));
            stage.show();

            // Close the login view if needed
            Stage loginStage = (Stage) registerLink.getScene().getWindow();
            loginStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
