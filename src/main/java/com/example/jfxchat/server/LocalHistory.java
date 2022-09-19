package com.example.jfxchat.server;

import java.io.*;
import java.util.List;

public class LocalHistory {

    public static void write(String message){
        try {
            message = replace(message);
            UserMessage m = new UserMessage(message);
            Retrieval.write(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<UserMessage> read() {
        System.out.println("LocalHistory.read");
        return Retrieval.read(100);
    }

    private static String replace(String message) {
        return message.replace("/message ", "");
    }
}
