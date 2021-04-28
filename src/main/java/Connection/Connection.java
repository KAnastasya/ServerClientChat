package Connection;

import Logs.Log;

import java.io.*;
import java.net.Socket;

public class Connection implements Closeable {


    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public void send(Message message) {
        synchronized (this.out) {
            try {
                out.writeObject(message);
            } catch (IOException e) {
                Log.LOG_CONNECTION.error("Ошибка отправки сообщения: " + e.getMessage());
            }
        }
    }

    public Message receive() {
        synchronized (this.in) {
            Message message = null;
            try {
                message = (Message) in.readObject();
            } catch (IOException e) {
                Log.LOG_CONNECTION.error("Ошибка приема сообщения: " + e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.LOG_CONNECTION.error("Ошибка входящего сообщения: " + e.getMessage());
            }
            return message;
        }
    }

    @Override
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}