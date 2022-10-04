package com.example.jfxchat.server;

import java.sql.SQLException;

public class RenameServiceImpl implements RenameService{
    @Override
    public void rename(String login, String nick) throws SQLException {
        UserData userData = DataBaseClass.readEXByLogin(login);
        userData.setNick(nick);
        DataBaseClass.updateEX(userData);
    }
}
