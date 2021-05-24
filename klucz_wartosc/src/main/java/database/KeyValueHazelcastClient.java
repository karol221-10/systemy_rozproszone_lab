package database;

import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KeyValueHazelcastClient implements KeyValueDatabaseConnector, KeyValueDatabaseInstance, KeyValueDatabaseProcessor{

    private HazelcastInstance hazelcastInstance;

    @Override
    public void connect(String hostname, Integer port) throws IOException {
        ClientConfig config = new ClientConfig();
        ClientNetworkConfig network = config.getNetworkConfig();
        network.addAddress(hostname);
        hazelcastInstance = HazelcastClient.newHazelcastClient( config );
    }

    @Override
    public Object getValue(String collection, String key) {
        return hazelcastInstance.getMap(collection).get(key);
    }

    @Override
    public <T> List<T> getAll(String collection) {
        return new ArrayList<>(hazelcastInstance.<String, T>getMap(collection).values());
    }

    @Override
    public boolean containsKey(String collection, String key) {
        return hazelcastInstance.getMap(collection).containsKey(key);
    }

    @Override
    public void setValue(String collection, String key, Object value) {
        hazelcastInstance.getMap(collection).set(key, value);
    }

    @Override
    public void removeValue(String collection, String key) {
        hazelcastInstance.getMap(collection).remove(key);
    }

    @Override
    public double calcAvgByField(String collection, String fieldName) {
        return hazelcastInstance.getMap(collection).aggregate(Aggregators.doubleAvg(fieldName));
    }
}
