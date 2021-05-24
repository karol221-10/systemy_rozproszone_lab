package database;

import java.util.List;

public interface KeyValueDatabaseInstance {
    Object getValue(String collection, String key);
    <T>List<T> getAll(String collection);
    boolean containsKey(String collection, String key);
    void setValue(String collection, String key, Object value);
    void removeValue(String collection, String key);
}
