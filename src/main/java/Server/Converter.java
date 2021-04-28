package Server;

import Logs.Log;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class Converter {

    private final File directory;
    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public Converter(File directory, File file) {
        this.directory = directory;
        this.file = file;
        mapper.registerModule(new JavaTimeModule());
    }

    public void toJSON(Map<Date, String> msg) {
        try {
            boolean folderAccessible = false;
            boolean fileAccessible = false;
            if (!directory.canExecute()) {
                folderAccessible = directory.mkdir();
            } else
                folderAccessible = true;
            if (!file.canExecute()) {
                fileAccessible = file.createNewFile();
            } else
                fileAccessible = true;
            if (folderAccessible && fileAccessible) {
                OutputStream out = new FileOutputStream(file);
                Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                mapper.writeValue(writer, msg);
            } else
                throw new FileNotFoundException("Файл хранилища не доступен или не создан");
        } catch (Exception e) {
            Log.LOG_CONVERTER.error("Ошибка преобразования: " + e.getMessage());
        }
    }

    public Map<Date, String> toJavaObject() {
        Map<Date, String> result = null;
        try {
            if (directory.canExecute() && file.canExecute()) {
                InputStream in = new FileInputStream(file);
                Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                result = mapper.readValue(reader, new TypeReference<Map<Date, String>>() {
                });
            }
        } catch (IOException e) {
            Log.LOG_CONVERTER.error("Ошибка преобразования: " + e.getMessage());
        }
        return result;
    }
}