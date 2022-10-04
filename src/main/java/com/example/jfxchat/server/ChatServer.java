package com.example.jfxchat.server;
//
import com.example.jfxchat.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChatServer {

    private final Map<String, ClientHandler> clients;

    public ChatServer() {
        this.clients = new HashMap<>();
    }

    public void  run() {
        try(ServerSocket serverSocket = new ServerSocket(8190);
            AuthService authService = new InMemoryAuthService()){
            while (true) {
                ChatLog.LOGGER.info("Ожидаю подключения...");
                Socket socket = serverSocket.accept();
                ChatLog.LOGGER.info("Сервер запущен!");
                new ClientHandler(socket, this, authService);
                ChatLog.LOGGER.info("Клиент подлюклеч!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.put(client.getNick(), client);
        broadcastClientsList();
    }

    private void broadcastClientsList() {
        String nicks = clients.values().stream()
                .map(ClientHandler::getNick)
                .collect(Collectors.joining( " "));
        broadcast(Command.CLIENTS, nicks);
    }

    public void broadcast(Command command, String message) {
        if(command == Command.MESSAGE) {
            LocalHistory.write(message);
        }

        for(ClientHandler client : clients.values() ){
            client.sendMessage(command, message);
            ChatLog.LOGGER.info("Клиент прислал сообщение!");
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.get(nick) != null;
    }

    public void unsubscribe(ClientHandler client) {
        clients.remove(client.getNick());
        broadcastClientsList();
    }

    public void sendPrivateMessage(ClientHandler from, String nickTo, String message) {
        ClientHandler clientTo = clients.get(nickTo);
        if (clientTo == null) {
            from.sendMessage(Command.ERROR, "Пользователь не авторизован!");
            return;
        }
        clientTo.sendMessage(Command.MESSAGE, "От "  + from.getNick() + ": " + message);
        from.sendMessage(Command.MESSAGE,"Участнику " + nickTo + ": " + message);
    }

    public void sendHistoryMessage(ClientHandler self, List<UserMessage> messages) {
        System.out.println("sendHistoryMessage");
        for(UserMessage message : messages) {
            System.out.println("message: " + message.getMessage());
            self.sendMessage(Command.MESSAGE, message.getMessage());
        }
    }
}
