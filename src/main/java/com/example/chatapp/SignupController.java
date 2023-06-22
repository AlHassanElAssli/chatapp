package com.example.chatapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SignupController {
    @FXML
    private TextField usernameInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private TextField confirmPasswordInput;
    @FXML
    private Button signupButton;
    @FXML
    private Hyperlink loginLink;
    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        // Initialization code for the controller
        errorLabel.setVisible(false); // Hide the error label initially
    }

    @FXML
    private void handleSignupButton() {
        String username = usernameInput.getText().trim();
        String password = passwordInput.getText().trim();
        String confirmPassword = confirmPasswordInput.getText().trim();

        // Validate the username, password, and confirm password
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            displayError("Please enter all required fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            displayError("Password and confirm password do not match.");
            return;
        }

        // Connect to the MySQL database
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatapp", "root", "root")) {
            // Check if the user already exists
            String checkUserQuery = "SELECT id FROM user WHERE username = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery)) {
                checkStatement.setString(1, username);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // User already exists
                        displayError("User already exists");
                        return;
                    }
                }
            }

            // Insert a new user into the user table
            String insertUserQuery = "INSERT INTO user (username, password) VALUES (?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(insertUserQuery)) {
                statement.setString(1, username);
                statement.setString(2, password);
                statement.executeUpdate();
            }

            // Get the user ID of the newly inserted user
            String selectUserIdQuery = "SELECT id FROM user WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(selectUserIdQuery)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("id");

                        // Insert a new profile for the user
                        String insertProfileQuery = "INSERT INTO profile (name, user_id) VALUES (?, ?)";
                        try (PreparedStatement profileStatement = connection.prepareStatement(insertProfileQuery)) {
                            profileStatement.setString(1, username);
                            profileStatement.setInt(2, userId);
                            profileStatement.executeUpdate();
                        }

                        // Successful signup
                        // Clear the input fields
                        usernameInput.clear();
                        passwordInput.clear();
                        confirmPasswordInput.clear();
                        displayError("User Created");

                        // Redirect to the login view or perform any other necessary actions
                    }
                }
            }
        } catch (SQLException e) {
            // Handle any database errors
            e.printStackTrace();
            displayError("Error creating user");
        }
    }
    // Method to clear the input fields
    private void clearInputFields() {
        usernameInput.clear();
        passwordInput.clear();
        confirmPasswordInput.clear();
    }

    // Method to display an error message
    private void displayError(String errorMessage) {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
    }

    @FXML
    private void handleLoginLink() {
        // Login link click handler
        loadLoginView();
    }

    private void loadLoginView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent loginView = fxmlLoader.load();
            Stage stage = new Stage();
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("loginIcon.png")).toExternalForm()));
            stage.setTitle("Login");
            stage.setScene(new Scene(loginView));
            stage.show();

            // Close the signup view if needed
            Stage signupStage = (Stage) loginLink.getScene().getWindow();
            signupStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
