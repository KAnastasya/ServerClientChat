package Server;

import Logs.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MessageServerWriter implements Runnable {
    private BufferedReader reader;
    private Server server;

    public MessageServerWriter(Server server) {
        this.server = server;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            Log.LOG_MESSAGE_SERVER_WRITER.error("Ошибка при чтении сообщения с консоли: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String messageText;
        while (true) {
            try {
                messageText = reader.readLine();
                if (messageText.equals("stop")) {
                    server.stopServer();
                } else if (messageText.equals("start")) {
                    server.startServer(8000);
                    server.acceptServer();
                }
            } catch (Exception e) {
                Log.LOG_MESSAGE_SERVER_WRITER.error("Ошибка ввода сообщения: " + e.getMessage());
            }
        }
    }
}