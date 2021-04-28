package Client;

import Logs.Log;

public class MessageReader implements Runnable {
    private Client client;

    public MessageReader(Client client) {
        this.client = client;
    }

    @Override
    public void run() {
        try {
            while (true) {
                client.receiveMessageFromServer();
            }
        }catch (Exception e){
            Log.LOG_MESSAGE_READER.error("Ошибка вывода сообщения: "+e.getMessage());
        }
    }
}