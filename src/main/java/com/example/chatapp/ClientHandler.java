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

    public void setName(String name) {
        this.name = name;
    }

    private String name;

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
            name = in.readLine();
            while (true) {
                String message = in.readLine();
                System.out.println(name + ": " + message);
                if (message.equalsIgnoreCase("exit")) {
                    server.clientDisconnected(this);
                    break;
                }
                server.broadcast(message, this);
            }
        } catch (IOException e) {
            try {
                client.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
}
