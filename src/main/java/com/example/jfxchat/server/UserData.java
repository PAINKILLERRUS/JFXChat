package com.example.jfxchat.server;

public class UserData {
    private Integer id;
    private String nick;
    private String login;
    private String password;


    public String getNick() {
        return nick;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public Integer getId() {
        return id;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public UserData(Integer id, String nick, String login, String password) {
        this.id = id;
        this.nick = nick;
        this.login = login;
        this.password = password;

    }
}
