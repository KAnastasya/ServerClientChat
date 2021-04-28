package Server;

import Logs.Log;

public class ServerMain {
    public static void main(String[] args) {
        Log.LOG_SERVER.debug("Starting server...");
        Server server = new Server();
        try {
            server.startServer(8000);
            server.acceptServer();
        } catch (Exception e) {
            Log.LOG_SERVER.error("Ошибка работы сервера: "+e.getMessage());
        }
    }
}
