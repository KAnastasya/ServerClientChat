package Client;

import Logs.Log;

import java.io.*;

public class MessageWriter implements Runnable {
    private BufferedReader reader;
    private Client client;

    public MessageWriter(Client client) {
        this.client = client;
        try {
            reader = new BufferedReader(new InputStreamReader(System.in));
        } catch (Exception e) {
            Log.LOG_MESSAGE_WRITER.error("Ошибка при чтении сообщения с консоли: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        String messageText;
        while (true) {
            try {
                messageText = reader.readLine();
                client.sendMessageOnServer(messageText);
                Log.LOG_MESSAGE_WRITER.debug("Message sended");
            }catch (Exception e){
                Log.LOG_MESSAGE_WRITER.error("Ошибка ввода сообщения: " + e.getMessage());
            }
        }
    }
}