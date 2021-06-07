import database.KeyValueHazelcastClient;

import java.io.IOException;

public class HClient {

    public static void main(String[] args) throws IOException {
        var keyValueHazelcastClient = new KeyValueHazelcastClient();
        keyValueHazelcastClient.connect("127.0.0.1", -1);
        Client.process(keyValueHazelcastClient, keyValueHazelcastClient);
    }
}
