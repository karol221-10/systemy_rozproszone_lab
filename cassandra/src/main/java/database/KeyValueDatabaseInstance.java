package database;

import java.util.List;

public interface KeyValueDatabaseInstance {
    <T> T getValue(String collection, String key, Class<T> className);
    int getObjectCount(String keyName);
    void updateObjectCount(String keyName, int newValue);
    <T>List<T> getAll(String collection, Class<T> className);
    <T> List<T> findBy(String collection, String fieldName, String fieldValue, Class<T> className);
    boolean containsKey(String collection, String key);
    <T> void setValue(String collection, String key, T value);
    <T> void updateValue(String collection, String key, T value);
    void removeValue(String collection, String key);
}
