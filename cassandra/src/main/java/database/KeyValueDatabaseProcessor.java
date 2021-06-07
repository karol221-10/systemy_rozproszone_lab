package database;

public interface KeyValueDatabaseProcessor {
    <T> double calcAvgByField(String collection, String fieldName, Class<T> objectName);
}
