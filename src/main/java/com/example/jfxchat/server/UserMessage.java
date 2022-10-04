package com.example.jfxchat.server;

import java.io.Serializable;

public class UserMessage implements Serializable {
    private String message;

    public UserMessage(String message) {
        this.message = message;
    }

    public UserMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
