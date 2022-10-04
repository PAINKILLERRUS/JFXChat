package com.example.jfxchat.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseClass {

    private static Connection connection;
    private static Statement stmt;

    public static void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:users.db");
        stmt = connection.createStatement();
    }

    public static void disconnect() {
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTableEx() throws SQLException {
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS users (\n" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                " nick TEXT,\n" +
                " login TEXT,\n" +
                " password TEXT\n" +
                ");");
    }

    public static void dropTableEx() throws SQLException {
        stmt.executeUpdate("DROP TABLE IF EXISTS users;");
    }

    public static List<UserData> readEX() throws SQLException {
        List<UserData> list = new ArrayList<>();
        try(ResultSet rs = stmt.executeQuery("SELECT * FROM users;")) {
            while (rs.next()) {
                UserData userData = new UserData(
                        rs.getInt("id"),
                        rs.getString("nick"),
                        rs.getString("login"),
                        rs.getString("password"));
                list.add(userData);
            }
        }
        return list;
    }

    public static UserData readByLoginAndPassword(String login, String password) {
        UserData userData = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE login = ? AND password = ?")) {
            preparedStatement.setString(1,login);
            preparedStatement.setString(2,password);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()){
                    userData = new UserData(
                            resultSet.getInt("id"),
                            resultSet.getString("nick"),
                            resultSet.getString("login"),
                            resultSet.getString("password")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static UserData readEXByLogin(String login){
        UserData userData = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE login = ?")) {
            preparedStatement.setString(1,login);
            try (ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next()) {
                    userData = new UserData(
                            resultSet.getInt("id"),
                            resultSet.getString("nick"),
                            resultSet.getString("login"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return userData;
    }

    public static UserData readById(int id){
        UserData userData = null;

        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM users WHERE id = ?")) {
            preparedStatement.setInt(1,id);
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    userData = new UserData(
                            resultSet.getInt("id"),
                            resultSet.getString("nick"),
                            resultSet.getString("login"),
                            resultSet.getString("password")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userData;
    }

    public static void clearTableEX() throws SQLException {
        stmt.executeUpdate("DELETE FROM users;");
    }

    public static void deleteEX(int id) throws SQLException {
        try(PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM users WHERE id = ?")) {
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserData updateEX(UserData userData) throws SQLException {
        try(PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE users SET nick = ? WHERE id = ?"
        )) {
            preparedStatement.setString(1, userData.getNick());
            preparedStatement.setInt(2,userData.getId());
            preparedStatement.executeUpdate();
            return userData;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UserData insertEX(UserData userData) throws SQLException {
        try(PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO users (nick, login, password) " +
                            "VALUES (?,?,?)")) {
            preparedStatement.setString(1, userData.getNick());
            preparedStatement.setString(2, userData.getLogin());
            preparedStatement.setString(3, userData.getPassword());
            preparedStatement.executeUpdate();
            return readEXByLogin(userData.getLogin());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
