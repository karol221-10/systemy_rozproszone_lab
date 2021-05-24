import database.KeyValueMemcachedClient;

import java.io.IOException;

public class MemcachedClient {

    public static void main(String[] args) throws IOException {
        var keyValueMemcachedClient = new KeyValueMemcachedClient();
        keyValueMemcachedClient.connect("127.0.0.1", 11211);
        Client.process(keyValueMemcachedClient, keyValueMemcachedClient);
    }
}
