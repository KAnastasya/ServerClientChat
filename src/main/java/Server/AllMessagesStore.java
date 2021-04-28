package Server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AllMessagesStore {

    private Map<Date, String> allMessages = new HashMap<>();

    protected Map<Date, String> getAllMessages() {
        return allMessages;
    }

    protected void setAllMessages(Map<Date, String> msg) {
        allMessages.putAll(msg);
    }

    protected void addMessage(Date date, String msg) {
        allMessages.put(date, msg);
    }

    protected void removeMessage(Date date, String msg) {
        allMessages.remove(date, msg);
    }
}