import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import database.KeyValueHazelcastClient;
import database.KeyValueMemcachedClient;
import datamanager.DataManager;
import datamanager.DataManagerFactory;
import dataprocessor.DataProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

public class HClient {

    public static void main(String[] args) throws IOException {
        var keyValueHazelcastClient = new KeyValueHazelcastClient();
        keyValueHazelcastClient.connect("127.0.0.1", -1);
        Client.process(keyValueHazelcastClient, keyValueHazelcastClient);
    }
}
