package Server;

import Logs.Log;
import Utils.PropertyReader;

public class ServerMain {
    public static void main(String[] args) {
        Log.LOG_SERVER.debug("Запуск сервера");
        PropertyReader propertyReader = new PropertyReader();
        propertyReader.readProperty("src/main/resources/serverConfig.properties");
        try {
            int port = new Integer(propertyReader.getProperty("port"));
            Server server = new Server(port, propertyReader.getProperty("storageFolderPath"), propertyReader.getProperty("storageFileName"));
            server.startServer();
            server.acceptServer();
        } catch (IllegalArgumentException e) {
            Log.LOG_SERVER.error("Невозможно запустить сервер, порт не найден в файле свойств. " + e.getMessage());
        } catch (Exception e) {
            Log.LOG_SERVER.error("Ошибка работы сервера: " + e.getMessage());
        }
    }
}
