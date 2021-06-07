import database.KeyValueMemcachedClient;
import database.KeyValueMongodbClient;

import java.io.IOException;

public class MongodbClient {

    public static void main(String[] args) throws IOException {
        var keyValueMongodbClient = new KeyValueMongodbClient("database");
        keyValueMongodbClient.connect("127.0.0.1", 27017);
        Client.process(keyValueMongodbClient, keyValueMongodbClient);
    }
}
