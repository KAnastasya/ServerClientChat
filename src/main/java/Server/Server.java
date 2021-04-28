package Server;

import Connection.*;
import Logs.Log;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {
    private ServerSocket serverSocket;
    private static AllUsersStore usersStore = new AllUsersStore();
    private static AllMessagesStore allMessagesStore = new AllMessagesStore();
    private static volatile boolean isServerStart = false;

    Converter converter;
    private int port;
    private Thread read;

    public Server(int port, String storagePath, String storageFileName) {
        this.port = port;
        converter = new Converter(new File(storagePath), new File(storagePath, storageFileName));
    }

    public void startServer() {
        Map<Date, String> messages = converter.toJavaObject();
        if (messages != null) {
            allMessagesStore.setAllMessages(messages);
        }
        try {
            serverSocket = new ServerSocket(port);
            isServerStart = true;
            Log.LOG_SERVER.debug("Сервер запущен");
            System.out.println("Сервер запущен.\n");
            read = new Thread(new MessageServerWriter(this));
            read.start();
        } catch (Exception e) {
            Log.LOG_SERVER.error("Не удалось запустить сервер: " + e.getMessage());
        }
    }

    public void stopServer() {
        converter.toJSON(allMessagesStore.getAllMessages());
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<String, Connection> users : usersStore.getAllUsersMultiChat().entrySet()) {
                    users.getValue().close();
                }
                serverSocket.close();
                usersStore.getAllUsersMultiChat().clear();
                Log.LOG_SERVER.debug("Сервер остановлен");
                System.out.println("Сервер остановлен.\n");
            } else
                System.out.println("Сервер не запущен - останавливать нечего!\n");
        } catch (Exception e) {
            Log.LOG_SERVER.error("Остановить сервер не удалось: " + e.getMessage());
        }
    }

    public void acceptServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            } catch (Exception e) {
                Log.LOG_SERVER.error("Связь с сервером потеряна: " + e.getMessage());
                break;
            }
        }
    }

    protected void sendMessageAllUsers(Message message) {
        for (Map.Entry<String, Connection> user : usersStore.getAllUsersMultiChat().entrySet()) {
            try {
                user.getValue().send(message);
            } catch (Exception e) {
                Log.LOG_SERVER.error("Ошибка отправки сообщения всем пользователям!: " + e.getMessage());
            }
        }
    }

    private class ServerThread extends Thread {
        private Socket socket;

        public ServerThread(Socket socket) {
            this.socket = socket;
        }

        private String requestAndAddingUser(Connection connection) {
            while (true) {
                try {
                    connection.send(new Message(MessageType.REQUEST_NAME_USER));
                    System.out.println("Запрос имени пользователя");
                    Message responseMessage = connection.receive();
                    String userName = responseMessage.getTextMessage();

                    if (responseMessage.getTypeMessage() == MessageType.USER_NAME && userName != null && !userName.isEmpty()) {
                        if (!usersStore.getAllUsersMultiChat().containsKey(userName) || usersStore.getAllUsersMultiChat().isEmpty()) {
                            usersStore.addUser(userName, connection);
                            System.out.println("Добавили пользователя в общий список");
                            Set<String> listUsers = new HashSet<>();
                            for (Map.Entry<String, Connection> users : usersStore.getAllUsersMultiChat().entrySet()) {
                                listUsers.add(users.getKey());
                            }
                            connection.send(new Message(MessageType.NAME_ACCEPTED, listUsers));
                            connection.send(new Message(MessageType.ALL_MESSAGES, allMessagesStore.getAllMessages()));
                            if (listUsers.size() > 1) {
                                sendMessageAllUsers(new Message(MessageType.USER_ADDED, userName));
                                sendMessageAllUsers(new Message(MessageType.ALL_USERS, listUsers));
                            }
                            return userName;
                        } else connection.send(new Message(MessageType.NAME_USED));
                    } else
                        System.out.println("Возможно имя пользователя пустое");
                } catch (Exception e) {
                    Log.LOG_SERVER.error("Возникла ошибка при запросе и добавлении нового пользователя: " + e.getMessage());
                }
            }
        }

        private void messagingBetweenUsers(Connection connection, String userName) {
            Message msgServerGreat = new Message(MessageType.TEXT_MESSAGE, "Добро пожаловать в чат!");
            sendMessageAllUsers(msgServerGreat);
            while (true) {
                try {
                    Message message = connection.receive();
                    if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                        Date msgDate = new Date();
                        String text = String.format("%s: %s\n", userName, message.getTextMessage());
                        allMessagesStore.addMessage(msgDate, text);

                        String textMessage = String.format("%s: %s\n", userName, message.getTextMessage());
                        sendMessageAllUsers(new Message(MessageType.TEXT_MESSAGE, textMessage));
                        System.out.println("Сообщение отправлено другим пользователям");
                    }

                    if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                        sendMessageAllUsers(new Message(MessageType.REMOVED_USER, userName));
                        usersStore.removeUser(userName);
                        connection.close();
                        System.out.printf("Пользователь с удаленным доступом %s отключился.\n%n", socket.getRemoteSocketAddress());
                        break;
                    }
                } catch (Exception e) {
                    Log.LOG_SERVER.error("Произошла ошибка при рассылке сообщения от пользователя, либо отключился! " + e.getMessage());
                    break;
                }
            }
        }

        @Override
        public void run() {
            System.out.printf("Подключился новый пользователь с удаленным сокетом - %s.\n%n", socket.getRemoteSocketAddress());
            Log.LOG_SERVER.debug("User connected with socket - " + socket.getRemoteSocketAddress());
            try {
                Connection connection = new Connection(socket);
                String nameUser = requestAndAddingUser(connection);
                messagingBetweenUsers(connection, nameUser);
            } catch (Exception e) {
                Log.LOG_SERVER.error("Произошла ошибка при рассылке сообщения от пользователя! " + e.getMessage());
            }
        }
    }
}