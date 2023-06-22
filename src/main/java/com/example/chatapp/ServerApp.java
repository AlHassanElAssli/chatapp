package com.example.chatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerApp {
    private int port;
    private ServerSocket server;
    private List<ClientHandler> clients;

    public ServerApp(int port) throws IOException {
        this.port = port;
        this.server = new ServerSocket(port);
        this.clients = new ArrayList<>();
    }

    public void createConnection() throws IOException {
        Socket client = server.accept();
        ClientHandler clientHandler = new ClientHandler(client, this);
        clients.add(clientHandler);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(clientHandler);
        executor.shutdown();
        System.out.println("User connected");
    }

    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.getPrintWriter().println(client.getName() + ": " + message);
            }
        }
    }

    public void clientDisconnected(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println(clientHandler.getName() + " has disconnected.");
        // Perform any additional actions or cleanup
    }

    public void start() {
        System.out.println("Server started");
        try {
            while (true) {
                createConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234; // Change to the desired port number
        try {
            ServerApp server = new ServerApp(port);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately
        }
    }
}
