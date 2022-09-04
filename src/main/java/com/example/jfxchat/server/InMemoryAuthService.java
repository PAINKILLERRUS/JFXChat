package com.example.jfxchat.server;
//
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {

    private List<UserData> users;
    public InMemoryAuthService() {
        users = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            users.add(new UserData( i, "nick" + i, "login" + i, "password" + i));
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        UserData userData = DataBaseClass.readByLoginAndPassword(login, password);
        return userData.getNick();
    }

    @Override
    public void close() throws IOException {
        System.out.println("Сервис аутентификации остановлен.");

    }
}
