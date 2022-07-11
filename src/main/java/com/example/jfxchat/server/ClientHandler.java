package com.example.jfxchat.server;
//
import com.example.jfxchat.Command;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientHandler {
    private Socket socket;
    private ChatServer server;
    private DataInputStream in;
    private DataOutputStream out;
    private String nick;
    private AuthService authService;
    private ScheduledExecutorService scheduler;
    private Thread socketThread;

    public ClientHandler(Socket socket, ChatServer server, AuthService authService) {
        try {
            this.server = server;
            this.socket = socket;
            this.authService = authService;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            socketThread = new Thread(()-> {
                try {
                    startTimer();
                    authenticate();
                    if(!Thread.currentThread().isInterrupted())
                        readMessages();
                } finally {
                    closeConnection();
                }
            });
            socketThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startTimer() {
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(() -> {
            socketThread.interrupt();
            sendMessage(Command.TIMEOUT, "Время для авторизации вышло!");
        }, 120, TimeUnit.SECONDS);
        scheduler.shutdown();
    }

    private void stopTimer() {
        scheduler.shutdownNow();
    }

    private void authenticate() {
        while (true) {

            if(Thread.currentThread().isInterrupted())
                break;

            try {
                String message = in.readUTF();
                    Command command = Command.getCommand(message);
                    if (command == Command.AUTH) {
                        String[] params = command.parse(message);
                        String login = params[0];
                        String password = params[1];
                        String nick = authService.getNickByLoginAndPassword(login, password);
                        if (nick != null) {
                            if (server.isNickBusy(nick)) {
                                sendMessage(Command.ERROR, "Пользователь уже авторизован!");
                                continue;
                            }
                            sendMessage(Command.AUTHOK, nick);
                            this.nick = nick;
                            server.broadcast(Command.MESSAGE, "Пользователь " + nick + " зашел в чат");
                            server.subscribe(this);
                            stopTimer();
                            break;
                        } else {
                            sendMessage(Command.ERROR, "Неверные логин и пароль!");
                        }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(Command command, String... params) {
        sendMessage(command.collectMessage(params));
    }

    private void closeConnection() {
        sendMessage(Command.END);
        if (in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null) {
            server.unsubscribe(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            out.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        while (true) {
            try {
                String message = in.readUTF();
                Command command = Command.getCommand(message);
                if (command == Command.END) {
                        break;
                }
                if (command == Command.PRIVATE_MESSAGE){
                    String[] params = command.parse(message);
                    server.sendPrivateMessage(this, params[0], params[1]);
                    continue;
                }
                server.broadcast(Command.MESSAGE,nick + ": " + command.parse(message)[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNick() {
        return nick;
    }
}
