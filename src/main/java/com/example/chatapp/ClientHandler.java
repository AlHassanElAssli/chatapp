package com.example.chatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private ServerApp server;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ClientHandler(Socket client, ServerApp server) throws IOException {
        this.client = client;
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        out = new PrintWriter(client.getOutputStream(), true);
        this.server = server;
    }

    public PrintWriter getPrintWriter() {
        return out;
    }

    @Override
    public void run() {
        try {
            // Read the client's name
            name = in.readLine();

            while (true) {
                // Read messages from the client
                String message = in.readLine();
                System.out.println(name + ": " + message);

                // Check if the client wants to exit
                if (message.equalsIgnoreCase("exit")) {
                    // Notify the server that the client has disconnected
                    server.clientDisconnected(this);
                    break;
                }

                // Broadcast the message to all connected clients
                server.broadcast(message, this);
            }
        } catch (IOException e) {
            try {
                // Close the client socket
                client.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
}
