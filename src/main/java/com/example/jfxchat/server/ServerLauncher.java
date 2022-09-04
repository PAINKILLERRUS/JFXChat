package com.example.jfxchat.server;

import java.sql.SQLException;

//
public class ServerLauncher {
    public static void main(String[] args) throws SQLException {
        new ChatServer().run();
        DataBaseClass.connect();
    }
}
