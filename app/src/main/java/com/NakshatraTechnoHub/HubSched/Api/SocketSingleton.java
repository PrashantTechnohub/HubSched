package com.NakshatraTechnoHub.HubSched.Api;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketSingleton {
    private static SocketSingleton instance;
    public Socket socket;

    private SocketSingleton() {
        try {
            socket = IO.socket(Constant.domain);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static synchronized SocketSingleton getInstance() {
        if (instance == null) {
            instance = new SocketSingleton();
        }
        return instance;
    }

    public io.socket.client.Socket getSocket() {
        return socket;
    }
}