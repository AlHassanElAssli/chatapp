package com.example.chatapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

public class ChatController {
    @FXML
    public AnchorPane chatPane;
    @FXML
    private TextField messageField;
    @FXML
    private Button sendButton;
    @FXML
    private ListView<String> messageListView;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    @FXML
    public void addMessage() throws IOException {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            messageListView.getItems().add("You: " + message);
            messageField.clear();
            messageListView.scrollTo(messageListView.getItems().size() - 1);

            // Check if the user input is "exit"
            if (message.equalsIgnoreCase("exit")) {
                // Disable the text field and button
                messageField.setDisable(true);
                sendButton.setDisable(true);
                out.println(message);
                // Close the socket
                try {
                    out.close();
                    in.close();
                    socket.close();
                } catch (ConnectException e) {
                    addMessageServer("Server is offline. Please try again later.");
                    messageField.setDisable(true);
                    sendButton.setDisable(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // Send the message to the server
                out.println(message);
            }
        }
    }

    @FXML
    public void addMessageServer(String message) {
        if (!message.isEmpty()) {
            Platform.runLater(() -> {
                messageListView.getItems().add(message);
                messageField.clear();
                messageListView.scrollTo(messageListView.getItems().size() - 1);
            });
        }
    }


    @FXML
    public void initialize() throws IOException {
        messageListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setText(null);
                    setBackground(null);
                } else {
                    setText(item);
                    setBackground(new Background(new BackgroundFill(Color.web("#2c2f33"), new CornerRadii(5), null)));
                    setTextFill(Color.WHITE);
                }
            }
        });

    }
    public void runClient (){
        try {
            socket = new Socket("localhost", 1234);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(username);
            // Start a separate thread for listening to server messages
            Thread listenThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        addMessageServer(message);
                    }
                } catch (IOException e) {
                    if (!(e instanceof SocketException && e.getMessage().equals("Socket closed"))) {
                        e.printStackTrace();
                    }
                }
            });
            listenThread.start();
            // Close the resources when the controller is destroyed
            Platform.runLater(() -> {
                sendButton.getScene().getWindow().setOnCloseRequest(event -> {
                    try {
                        out.println("exit");
                        out.close();
                        in.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (ConnectException e) {
            addMessageServer("Server is offline. Please try again later.");
            sendButton.setDisable(true);
            messageField.setDisable(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setUsername(String username) {
        this.username = username;
    }
}

