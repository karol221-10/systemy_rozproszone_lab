package datamanager;

import database.KeyValueDatabaseInstance;
import lombok.RequiredArgsConstructor;
import model.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@RequiredArgsConstructor
public class ClientDataManager implements DataManager {

    private final KeyValueDatabaseInstance instance;

    public static final String COLLECTION_NAME = "clients";

    @Override
    public void add() throws IOException {
        Client.ClientBuilder clientBuilder = Client.builder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Wypełnij dane dodawanego klienta");
        System.out.print("Podaj imię klienta: ");
        String clientName = reader.readLine();
        clientBuilder.name(clientName);
        System.out.print("Podaj nazwisko klienta: ");
        String clientSurname = reader.readLine();
        clientBuilder.surname(clientSurname);
        System.out.print("Podaj wiek klienta: ");
        clientBuilder.age(Integer.parseInt(reader.readLine()));
        var idPrefix = clientName.substring(0,3) + clientSurname.substring(0, 3);
        var clientCount = instance.getObjectCount(idPrefix);
        var id = idPrefix + (clientCount + 1);
        clientBuilder._id(id);
        var client = clientBuilder.build();
        clientCount++;
        instance.updateObjectCount(idPrefix, clientCount);
        instance.setValue(COLLECTION_NAME, id, client);
        System.out.println("Dodano klienta o uuid: " + id);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja klienta");
        System.out.print("Podaj identyfikator aktualizowanego klienta: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey(COLLECTION_NAME, identifier)) {
            System.out.println("Nie znaleziono klienta o uuid " + identifier);
            return;
        }
        var client = instance.getValue(COLLECTION_NAME, identifier, Client.class);
        System.out.print("Podaj imię klienta ("+client.getName()+") :");
        String newName = reader.readLine();
        if(!newName.isBlank()) {
            client.setName(newName);
        }
        System.out.print("Podaj nazwisko klienta ("+client.getSurname()+"): ");
        String newSurname = reader.readLine();
        if(!newSurname.isBlank()) {
            client.setSurname(newSurname);
        }
        System.out.print("Podaj wiek klienta ("+client.getAge()+"): ");
        String age = reader.readLine();
        if(!age.isBlank()) {
            client.setAge(Integer.parseInt(age));
        }
        instance.updateValue(COLLECTION_NAME, identifier, client);
        System.out.println("Zaktualizowano dane klienta o id: " + identifier);
    }

    @Override
    public void remove() throws IOException {
        System.out.println("Usuwanie klienta");
        System.out.print("Podaj identyfikator klienta: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        System.out.println("Klient o UUID " + identifier + " został usunięty");
        instance.removeValue(COLLECTION_NAME, identifier);
    }

    @Override
    public void show() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj identyfikator klienta: ");
        var identifier = reader.readLine();
        if(!instance.containsKey("clients", identifier)) {
            System.out.println("W bazie nie istnieje obiekt o identyfikatorze " + identifier);
            return;
        }
        System.out.println("Pobrano obiekt: ");
        var client = instance.getValue(COLLECTION_NAME, identifier, Client.class);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Imię: " + client.getName());
        System.out.println("Nazwisko: " + client.getSurname());
        System.out.println("Wiek: " + client.getAge());
    }
}
