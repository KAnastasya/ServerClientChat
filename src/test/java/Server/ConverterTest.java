package Server;

import org.junit.Test;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class ConverterTest {

    @Test
    public void toJavaObject() {
        String directory = "src/test/resources";
        String file = "MessageStoreTest";
        Converter converter = new Converter(new File(directory), new File(directory, file));
        Map<Date, String> expected = converter.toJavaObject();

        assertTrue(expected.containsValue("Anna: I am fine\n"));
    }
}