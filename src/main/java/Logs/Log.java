package Logs;


import org.apache.logging.log4j.*;

public class Log {
    public static final Logger rootLogger = LogManager.getRootLogger();
    public static final Logger LOG_CLIENT = LogManager.getLogger("Client");
    public static final Logger LOG_SERVER = LogManager.getLogger("Server");
    public static final Logger LOG_CONNECTION = LogManager.getLogger("Connection");
    public static final Logger LOG_MESSAGE_WRITER = LogManager.getLogger("MessageWriter");
    public static final Logger LOG_MESSAGE_READER = LogManager.getLogger("MessageReader");
    public static final Logger LOG_CONVERTER = LogManager.getLogger("Converter");
    public static final Logger LOG_MESSAGE_SERVER_WRITER = LogManager.getLogger("MessageServerWriter");
    public static final Logger LOG_PROPERTY_READER = LogManager.getLogger("PropertyReader");
}
