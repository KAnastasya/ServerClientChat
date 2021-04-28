package Connection;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public class Message implements Serializable {
    private MessageType typeMessage;
    private String textMessage;
    private Set<String> listUsers;
    private Map<Date, String> allMessages;

    public Message(MessageType typeMessage, String textMessage) {
        this.textMessage = textMessage;
        this.typeMessage = typeMessage;
        this.listUsers = null;
    }

    public Message(MessageType typeMessage, Set<String> listUsers) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = listUsers;
    }

    public Message(MessageType typeMessage) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = null;
    }

    public Message(MessageType typeMessage, Map<Date, String> allMessages) {
        this.typeMessage = typeMessage;
        this.textMessage = null;
        this.listUsers = null;
        this.allMessages = allMessages;
    }

    public MessageType getTypeMessage() {
        return typeMessage;
    }

    public Set<String> getListUsers() {
        return listUsers;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public Map<Date, String> getAllMessages() {
        return allMessages;
    }
}
