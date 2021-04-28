package Utils;

import Logs.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
    private FileInputStream fis;
    private final Properties property = new Properties();


    public void readProperty(String path) {
        try {
            fis = new FileInputStream(path);
            property.load(fis);
        } catch (IOException e) {
            Log.LOG_PROPERTY_READER.error("Файл свойств отсуствует!");
        }
    }

    public String getProperty(String name) throws IllegalArgumentException {
        if (name == null) {
            Log.LOG_PROPERTY_READER.error("Невозможно прочитать свойство, имя свойства null");
            throw new IllegalArgumentException("Невозможно прочитать свойство, имя свойства null");
        }
        if (!property.containsKey(name)) {
            Log.LOG_PROPERTY_READER.error("Невозможно прочитать свойство, свойство с имененм: " + name + " не найдено");
            throw new IllegalArgumentException("Невозможно прочитать свойство, свойство с имененм: " + name + " не найдено");
        }
        return property.getProperty(name);
    }
}
