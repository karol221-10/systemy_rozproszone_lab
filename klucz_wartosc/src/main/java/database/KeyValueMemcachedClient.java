package database;

import lombok.val;
import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class KeyValueMemcachedClient implements KeyValueDatabaseConnector, KeyValueDatabaseInstance, KeyValueDatabaseProcessor {
    
    private MemcachedClient memcachedClient;
    
    @Override
    public void connect(String hostname, Integer port) throws IOException {
        memcachedClient = new MemcachedClient(new InetSocketAddress(hostname, port));
    }

    @Override
    public Object getValue(String collection, String key) {
        return memcachedClient.get(collection+"_"+key);
    }

    @Override
    public List<Object> getAll(String collection) {
        val keys = (ArrayList<String>) Optional.ofNullable(memcachedClient.get(collection)).orElse(new ArrayList<String>());
        return keys.stream()
                .map(key -> memcachedClient.get(collection+"_"+key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsKey(String collection, String key) {
        return memcachedClient.get(collection + "_" + key) != null;
    }

    @Override
    public void setValue(String collection, String key, Object value) {
        memcachedClient.set(collection+"_"+key, 2000000000, value);
        ArrayList<String> keys = (ArrayList<String>) Optional.ofNullable(memcachedClient.get(collection)).orElse(new ArrayList<String>());
        keys.add(key);
        memcachedClient.set(collection, 2000000000, keys);
    }

    @Override
    public void removeValue(String collection, String key) {
        memcachedClient.delete(collection+"_"+key);
        ArrayList<String> keys = (ArrayList<String>) Optional.ofNullable(memcachedClient.get(collection)).orElse(new ArrayList<String>());
        keys.remove(key);
        memcachedClient.set(collection, 2000000000, keys);
    }

    @Override
    public double calcAvgByField(String collection, String fieldName) {
        ArrayList<String> allKeys = (ArrayList<String>) Optional.ofNullable(memcachedClient.get(collection)).orElse(new ArrayList<String>());
        ArrayList<Double> values = new ArrayList<>();
        allKeys.forEach(key -> {
            val object = memcachedClient.get(collection+"_"+key);
            if(object != null) {
                try {
                    Field field = object.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    values.add((Double)field.get(object));
                    field.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        });
        val sum = values.stream()
                .reduce(Double::sum).get();
        return sum / (long) values.size();
    }
}
