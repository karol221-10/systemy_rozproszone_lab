package datamanager;

import database.KeyValueDatabaseInstance;
import lombok.RequiredArgsConstructor;
import model.Courier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;


@RequiredArgsConstructor
public class CourierDataManager implements DataManager {

    private final KeyValueDatabaseInstance instance;

    public void add() throws IOException {
        Courier.CourierBuilder courierBuilder = Courier.builder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Wypełnij dane dodawanego kuriera");
        System.out.print("Podaj imię kuriera: ");
        String name = reader.readLine();
        courierBuilder.name(name);
        System.out.print("Podaj nazwisko kuriera: ");
        String surname = reader.readLine();
        courierBuilder.surname(surname);
        System.out.print("Podaj wiek kuriera");
        courierBuilder.age(Double.parseDouble(reader.readLine()));
        var idPrefix = name.substring(0,3) + surname.substring(0, 3);
        var courierCount = (Integer) Optional.ofNullable(instance.getValue("count",idPrefix)).orElse(0);
        var courier = courierBuilder.build();
        var uuid = idPrefix + (courierCount + 1);
        courierCount++;
        instance.setValue("count", idPrefix, courierCount);
        instance.setValue("couriers", uuid, courier);
        System.out.println("Dodano kuriera o uuid: " + uuid);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja kuriera");
        System.out.print("Podaj identyfikator aktualizowanego kuriera: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey("couriers", identifier)) {
            System.out.println("Nie znaleziono kuriera o uuid " + identifier);
            return;
        }
        var courier = (Courier)instance.getValue("couriers", identifier);
        System.out.print("Podaj imię kuriera ("+courier.getName()+") :");
        String newName = reader.readLine();
        if(!newName.isBlank()) {
            courier.setName(newName);
        }
        System.out.print("Podaj nazwisko kuriera ("+courier.getSurname()+"): ");
        String newSurname = reader.readLine();
        if(!newSurname.isBlank()) {
            courier.setSurname(newSurname);
        }
        System.out.print("Podaj wiek klienta ("+courier.getAge() + "): ");
        String newAge = reader.readLine();
        if(!newAge.isBlank()) {
            courier.setAge(Double.parseDouble(newAge));
        }
        instance.setValue("couriers", identifier, courier);
        System.out.println("Zaktualizowano dane kuriera o id: " + identifier);
    }

    @Override
    public void remove() throws IOException {
        System.out.println("Usuwanie kuriera");
        System.out.print("Podaj identyfikator kuriera: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        instance.removeValue("couriers", identifier);
        System.out.println("Kurier o UUID " + identifier + " został usunięty");
    }

    @Override
    public void show() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj identyfikator kuriera");
        var identifier = reader.readLine();
        if(!instance.containsKey("couriers", identifier)) {
            System.out.println("W bazie nie istnieje obiekt o identyfikatorze " + identifier);
            return;
        }
        System.out.println("Pobrano obiekt: ");
        var courier = (Courier) instance.getValue("couriers", identifier);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Imię: " + courier.getName());
        System.out.println("Nazwisko: " + courier.getSurname());
        System.out.println("Wiek: " + courier.getAge());
    }
}
