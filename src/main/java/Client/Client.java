package Client;

import Connection.*;
import Logs.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Client {
    private String serverAddress;
    private int port;
    private Connection connection;
    private AllClientsSet allClientsSet = new AllClientsSet();
    private Map<Date, String> allMessages = new HashMap<>();
    private volatile boolean isConnect = false;
    private Thread read;
    private Thread write;

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public Client(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;
        this.initializeClient(serverAddress, port);
    }

    public void nameUserRegistration() {
        while (true) {
            try {
                Message message = connection.receive();

                if (message.getTypeMessage() == MessageType.REQUEST_NAME_USER) {
                    System.out.println("Введите имя пользователя");
                    Scanner in = new Scanner(System.in);
                    String nameUser = in.next();
                    Message message1 = new Message(MessageType.USER_NAME, nameUser);
                    connection.send(message1);
                }

                if (message.getTypeMessage() == MessageType.NAME_USED) {
                    System.out.println("Данное имя уже используется, введите другое");
                    Scanner in = new Scanner(System.in);
                    String nameUser = in.nextLine();
                    connection.send(new Message(MessageType.USER_NAME, nameUser));
                }

                if (message.getTypeMessage() == MessageType.NAME_ACCEPTED) {
                    System.out.println("Сервисное сообщение: ваше имя принято!\n");
                    allClientsSet.setUsers(message.getListUsers());
                    break;
                }
            } catch (Exception e) {
                Log.LOG_CLIENT.error("Произошла ошибка при регистрации имени. Попробуйте переподключиться: " + e.getMessage());
                try {
                    connection.close();
                    isConnect = false;
                    break;
                } catch (IOException ex) {
                    Log.LOG_CLIENT.error("Ошибка при закрытии соединения " + e.getMessage());
                }
            }
        }
    }

    public void connectToServer(String serverAddress, int port) {
        if (!isConnect) {
            while (true) {
                try {
                    Log.LOG_CLIENT.debug("Попытка подключения к серверу: " + serverAddress + ":" + port);
                    Socket socket = new Socket(serverAddress, port);
                    connection = new Connection(socket);
                    isConnect = true;
                    System.out.println("Вы подключились к серверу.\n");
                    Log.LOG_CLIENT.debug("Произошло подключение к серверу: " + serverAddress + ":" + port);
                    break;
                } catch (Exception e) {
                    Log.LOG_CLIENT.error("Произошла ошибка! Возможно нет соединения с сервером. " + e.getMessage());
                    break;
                }
            }
        } else System.out.println("Вы уже подключены!");
    }

    public void sendMessageOnServer(String msg) {
        if (isConnect) {
            try {
                if (msg.equals("stop")) {
                    this.disableClient();
                } else
                    connection.send(new Message(MessageType.TEXT_MESSAGE, msg));
            } catch (Exception e) {
                Log.LOG_CLIENT.error("Ошибка при отправке сообщения " + e.getMessage());
            }
        } else {
            System.out.println("Невозможно отправить сообщение. Попытка повторного подключения к серверу");
            try {
                this.initializeClient(serverAddress, port);
            } catch (Exception e) {
                Log.LOG_CLIENT.error("Ошибка при отправке сообщения " + e.getMessage());
            }
        }
    }

    public void receiveMessageFromServer() {
        while (isConnect) {
            try {
                Message message = connection.receive();
                if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                    System.out.println(message.getTextMessage());
                    break;
                }

                if (message.getTypeMessage() == MessageType.ALL_MESSAGES) {
                    allMessages = this.sortMessages(message.getAllMessages());
                    System.out.println(allMessages);
                    break;
                }

                if (message.getTypeMessage() == MessageType.ALL_USERS) {
                    System.out.println("Список активных пользователей в чате: " + message.getListUsers());
                    break;
                }

                if (message.getTypeMessage() == MessageType.USER_ADDED) {
                    allClientsSet.addUser(message.getTextMessage());
                    System.out.println("Сервисное сообщение: новый пользователь присоединился к чату:\n" + message.getTextMessage());
                }

                if (message.getTypeMessage() == MessageType.REMOVED_USER) {
                    allClientsSet.removeUser(message.getTextMessage());
                    System.out.println("Сервисное сообщение: пользователь покинул чат:\n" + message.getTextMessage());
                }
            } catch (Exception e) {
                Log.LOG_CLIENT.error("Ошибка при приеме сообщения от сервера. " + e.getMessage());
                setConnect(false);
                break;
            }
        }
    }

    public void disableClient() {
        try {
            if (isConnect) {
                connection.send(new Message(MessageType.DISABLE_USER));
                allClientsSet.getUsers().clear();
                isConnect = false;

            } else System.out.println("Вы уже отключены.");
        } catch (Exception e) {
            Log.LOG_CLIENT.error("Сервисное сообщение: произошла ошибка при отключении. " + e.getMessage());
        }
    }

    public Map<Date, String> sortMessages(Map<Date, String> allMessages) {
        Map<Date, String> sortedMap = new TreeMap<>(Comparator.comparing(Date::toString));
        sortedMap.putAll(allMessages);
        return sortedMap;
    }

    public Set<String> getClientsSet() {
        return allClientsSet.getUsers();
    }

    private void initializeClient(String serverAddress, int port) {
        try {
            this.connectToServer(serverAddress, port);
            this.nameUserRegistration();
            read = new Thread(new MessageReader(this));
            read.start();
            write = new Thread(new MessageWriter(this));
            write.start();
        } catch (Exception e) {
            Log.LOG_CLIENT.error("Ошибка инициализации пользователя" + e.getMessage());
        }
    }
}