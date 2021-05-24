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
        System.out.print("Podaj miasto klienta: ");
        clientBuilder.city(reader.readLine());
        var idPrefix = clientName.substring(0,3) + clientSurname.substring(0, 3);
        var clientCount = (Integer) Optional.ofNullable(instance.getValue("count",idPrefix)).orElse(0);
        var client = clientBuilder.build();
        var id = idPrefix + (clientCount + 1);
        clientCount++;
        instance.setValue("count",idPrefix, clientCount);
        instance.setValue("clients", id, client);
        System.out.println("Dodano klienta o uuid: " + id);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja klienta");
        System.out.print("Podaj identyfikator aktualizowanego klienta: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey("clients", identifier)) {
            System.out.println("Nie znaleziono klienta o uuid " + identifier);
            return;
        }
        var client = (Client)instance.getValue("clients", identifier);
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
        System.out.print("Podaj miasto klienta ("+client.getCity()+"): ");
        String newCity = reader.readLine();
        if(!newCity.isBlank()) {
            client.setCity(newCity);
        }
        instance.setValue("clients", identifier, client);
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
        instance.removeValue("clients", identifier);
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
        var client = (Client)instance.getValue("clients", identifier);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Imię: " + client.getName());
        System.out.println("Nazwisko: " + client.getSurname());
        System.out.println("Miasto: " + client.getCity());
    }
}
