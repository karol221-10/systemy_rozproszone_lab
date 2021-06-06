package datamanager;

import database.KeyValueDatabaseInstance;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Librarian;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Optional;


@RequiredArgsConstructor
public class LibrarianDataManager implements DataManager {

    private final KeyValueDatabaseInstance instance;
    public static final String COLLECTION_NAME = "librarians";

    public void add() throws IOException {
        Librarian.LibrarianBuilder librarianBuilder = Librarian.builder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Wypełnij dane dodawanego bibliotekarza");
        System.out.print("Podaj imię bibliotekarza: ");
        String name = reader.readLine();
        librarianBuilder.name(name);
        System.out.print("Podaj nazwisko bibliotekarza: ");
        String surname = reader.readLine();
        librarianBuilder.surname(surname);
        System.out.print("Podaj datę zatrudnienia bibliotekarza");
        val dateOfEmployment = reader.readLine();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            librarianBuilder.dateOfEmployment(dateFormat.parse(dateOfEmployment).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } catch (ParseException e) {
            System.out.println("Podano datę w nieoczekiwanym formacie: " + dateOfEmployment + " Oczekiwano: " + "yyyy-MM-dd");
            return;
        }
        var idPrefix = name.substring(0,3) + surname.substring(0, 3);
        var librarianCount = (Integer) instance.getObjectCount(idPrefix);
        var uuid = idPrefix + (librarianCount + 1);
        librarianBuilder._id(uuid);
        var librarian = librarianBuilder.build();
        librarianCount++;
        instance.updateObjectCount(idPrefix, librarianCount);
        instance.setValue(COLLECTION_NAME, uuid, librarian);
        System.out.println("Dodano kuriera o uuid: " + uuid);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja bibliotekarza");
        System.out.print("Podaj identyfikator aktualizowanego bibliotekarza: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey(COLLECTION_NAME, identifier)) {
            System.out.println("Nie znaleziono bibliotekarza o uuid " + identifier);
            return;
        }
        var librarian = instance.getValue(COLLECTION_NAME, identifier, Librarian.class);
        System.out.print("Podaj imię  ("+librarian.getName()+") :");
        String newName = reader.readLine();
        if(!newName.isBlank()) {
            librarian.setName(newName);
        }
        System.out.print("Podaj nazwisko ("+librarian.getSurname()+"): ");
        String newSurname = reader.readLine();
        if(!newSurname.isBlank()) {
            librarian.setSurname(newSurname);
        }
        System.out.print("Podaj datę zatrudnienia bibliotekarza ("+librarian.getDateOfEmployment() + "): ");
        val dateOfEmployment = reader.readLine();
        if(!dateOfEmployment.isBlank()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                librarian.setDateOfEmployment(dateFormat.parse(dateOfEmployment).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } catch (ParseException e) {
                System.out.println("Podano datę w nieoczekiwanym formacie: " + dateOfEmployment + " Oczekiwano: " + "yyyy-MM-dd");
                return;
            }
        }
        instance.updateValue(COLLECTION_NAME, identifier, librarian);
        System.out.println("Zaktualizowano dane bibliotekarza o id: " + identifier);
    }

    @Override
    public void remove() throws IOException {
        System.out.println("Usuwanie kuriera");
        System.out.print("Podaj identyfikator bibliotekarza: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        instance.removeValue(COLLECTION_NAME, identifier);
        System.out.println("Bibliotekarz o UUID " + identifier + " został usunięty");
    }

    @Override
    public void show() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj identyfikator Bibliotekarza");
        var identifier = reader.readLine();
        if(!instance.containsKey(COLLECTION_NAME, identifier)) {
            System.out.println("W bazie nie istnieje obiekt o identyfikatorze " + identifier);
            return;
        }
        System.out.println("Pobrano obiekt: ");
        var librarian = instance.getValue(COLLECTION_NAME, identifier, Librarian.class);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Imię: " + librarian.getName());
        System.out.println("Nazwisko: " + librarian.getSurname());
        System.out.println("Data zatrudnienia: " + librarian.getDateOfEmployment());
    }
}
