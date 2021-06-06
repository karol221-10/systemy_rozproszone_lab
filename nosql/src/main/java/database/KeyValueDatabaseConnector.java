package database;

import java.io.IOException;

public interface KeyValueDatabaseConnector {
    void connect(String hostname, Integer port) throws IOException;
}
