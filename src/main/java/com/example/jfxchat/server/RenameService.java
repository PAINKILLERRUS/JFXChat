package com.example.jfxchat.server;

import java.sql.SQLException;

public interface RenameService {
    void rename(String login, String nick) throws SQLException;
}
