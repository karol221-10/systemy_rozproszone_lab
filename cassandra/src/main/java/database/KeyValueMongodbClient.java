package database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.val;
import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mongodb.client.model.Filters.eq;

public class KeyValueMongodbClient implements KeyValueDatabaseInstance, KeyValueDatabaseConnector, KeyValueDatabaseProcessor{

    private MongoClient mongoClient;
    private MongoDatabase db;
    private String databaseName;
    private ObjectMapper objectMapper;

    public KeyValueMongodbClient(String databaseName) {
        this.databaseName = databaseName;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public void connect(String hostname, Integer port) throws IOException {
        String clientURI = "mongodb://" + hostname + ":" + port;
        MongoClientURI mongoClientURI = new MongoClientURI(clientURI);
        mongoClient = new MongoClient(mongoClientURI);
        db = mongoClient.getDatabase(databaseName);

    }

    @Override
    public <T> T getValue(String collection, String key, Class<T> className) {
        val document = db.getCollection(collection).find(eq("_id", key));

        try {
            if(document.first() == null) {
                return null;
            }
            return objectMapper.readValue(Objects.requireNonNull(document.first()).toJson(), className);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getObjectCount(String keyName) {
        val document = db.getCollection("count").find(eq("_id", keyName));
        if(document.first() == null) {
            return 0;
        }
        return document.first().get("value", 0);
    }

    @Override
    public void updateObjectCount(String keyName, int newValue) {
        val document = db.getCollection("count").find(eq("_id", keyName));
        if(document.first() == null) {
            val newDocument = new Document("_id", keyName);
            newDocument.append("value", newValue);
            db.getCollection("count").insertOne(newDocument);
        }
        else {
            val updatedDocument = new Document("_id", keyName);
            updatedDocument.append("value", newValue);
            db.getCollection("count").replaceOne(eq("_id", keyName), updatedDocument);
        }
    }

    @Override
    public <T> List<T> getAll(String collection, Class<T> className) {
        return null;
    }

    @Override
    public <T> List<T> findBy(String collection, String fieldName, String fieldValue, Class<T> className) {
        List<T> resultList = new ArrayList<T>();
        val documentIterable = db.getCollection(collection).find(eq(fieldName, fieldValue));
        val it = documentIterable.iterator();
        while(it.hasNext()) {
            val document = it.next();
            try {
                resultList.add(objectMapper.readValue(Objects.requireNonNull(document).toJson(), className));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        return resultList;
    }

    @Override
    public boolean containsKey(String collection, String key) {
        return db.getCollection(collection).countDocuments(eq("_id", key)) > 0;
    }

    @Override
    public <T> void setValue(String collection, String key, T value) {
        val document = db.getCollection(collection);
        try {
            val object = objectMapper.writeValueAsString(value);
            document.insertOne(Document.parse(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public <T> void updateValue(String collection, String key, T value) {
        val documentCollection = db.getCollection(collection);
        try {
            val object = objectMapper.writeValueAsString(value);
            documentCollection.replaceOne(eq("_id", key), Document.parse(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeValue(String collection, String key) {
        val document = db.getCollection(collection);
        document.deleteOne(eq("_id", key));
    }

    @Override
    public <T> double calcAvgByField(String collection, String fieldName, Class<T> objectName) {
        List<Double> doubles = new ArrayList<>();
        val documentCollection = db.getCollection(collection).find();
        val it = documentCollection.iterator();
        while(it.hasNext()) {
            val document = it.next();
            doubles.add(Double.valueOf(document.getInteger(fieldName)));
        }
        return doubles.stream()
                .mapToDouble(t -> t)
                .average()
                .orElse(Double.NaN);
    }
}
