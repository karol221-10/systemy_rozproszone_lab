import database.KeyValueCassandraClient;

import java.io.IOException;

public class CassandraClient {
    public static void main(String[] args) throws IOException {
        var keyValueCassandraClient = new KeyValueCassandraClient();
        keyValueCassandraClient.connect(null, null);
        Client.process(keyValueCassandraClient, keyValueCassandraClient);
    }
}
