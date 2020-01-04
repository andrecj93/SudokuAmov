package com.example.sudokuamov.sockets;

import java.net.Socket;

public class ClientPlayers {
    final public Socket socket;
    public String Ip;
    public String Name;

    public ClientPlayers(final Socket socket) {
        this.socket = socket;
        Ip = socket.getInetAddress().toString().replace(".", "-").replace("/", "");
    }
}
