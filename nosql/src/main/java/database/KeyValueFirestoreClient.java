package database;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import lombok.val;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class KeyValueFirestoreClient implements KeyValueDatabaseConnector, KeyValueDatabaseProcessor, KeyValueDatabaseInstance{

    private Firestore db;
    private ObjectMapper objectMapper;

    public KeyValueFirestoreClient() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public void connect(String hostname, Integer port) throws IOException {
        val serviceAccount = getClass().getClassLoader().getResourceAsStream(hostname);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        val app = FirebaseApp.initializeApp(options);
        db = FirestoreClient.getFirestore();
    }

    @Override
    public <T> T getValue(String collection, String key, Class<T> className) {
        try {
             val object = db.collection(collection).document(key).get().get().getData();
             return objectMapper.convertValue(object, className);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getObjectCount(String keyName) {
        try {
            Long result = (Long)db.collection("count").document(keyName).get().get().get("value");
            if(result == null) {
                return 0;
            }
            return result.intValue();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateObjectCount(String keyName, int newValue) {
        val map = new HashMap<String, Integer>();
        map.put("value", newValue);
        db.collection("count").document(keyName).set(map);
    }

    @Override
    public <T> List<T> getAll(String collection) {
        return null;
    }

    @Override
    public <T> List<T> findBy(String collection, String fieldName, String fieldValue, Class<T> className) {
        try {
            return db.collection(collection).whereEqualTo(fieldName, fieldValue).get().get().getDocuments().stream()
                    .map(object -> object.toObject(className))
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean containsKey(String collection, String key) {
        try {
            return db.collection(collection).document(key).get().get().exists();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public <T> void setValue(String collection, String key, T value) {
        Map<String, Object> map = objectMapper.convertValue(value, Map.class);
        db.collection(collection).document(key).set(map);
    }

    @Override
    public <T> void updateValue(String collection, String key, T value) {
        setValue(collection, key, value);
    }

    @Override
    public void removeValue(String collection, String key) {
        try {
            db.collection(collection).document(key).delete().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> double calcAvgByField(String collection, String fieldName, Class<T> objectName) {
        List<Double> objects = new ArrayList<>();
        db.collection(collection).listDocuments().forEach(documentReference -> {
            try {
                val value = documentReference.get().get().get(fieldName);
                objects.add(Double.valueOf(value.toString()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return objects.stream()
                .mapToDouble(object -> object)
                .average()
                .getAsDouble();
    }
}
