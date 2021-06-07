import database.KeyValueFirestoreClient;
import lombok.val;

import java.io.IOException;

public class FirestoreClient {

    public static void main(String[] args) throws IOException {
        val firestoreClient = new KeyValueFirestoreClient();
        firestoreClient.connect("laboratoria-projekt.json",null);
        Client.process(firestoreClient, firestoreClient);
    }
}
