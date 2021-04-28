package Client;

import Logs.Log;
import Utils.PropertyReader;

public class ClientMain {
    public static void main(String[] args) {
        Log.LOG_CLIENT.debug("Запуск клиента");
        PropertyReader propertyReader = new PropertyReader();
        propertyReader.readProperty("src/main/resources/clientConfig.properties");
        try {
            int port = new Integer(propertyReader.getProperty("port"));
            String host = propertyReader.getProperty("host");
            Client client = new Client(host, port);
        } catch (IllegalArgumentException e) {
            Log.LOG_CLIENT.error("Невозможно запустить клиент, порт или адрес сервера не найден в файле свойств. " + e.getMessage());
        } catch (Exception e) {
            Log.LOG_CLIENT.error("Ошибка работы клиента: " + e.getMessage());
        }
    }
}