package datamanager;

import database.KeyValueDatabaseInstance;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Librarian;
import model.Client;
import model.BookBorrow;
import model.OperationKind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@RequiredArgsConstructor
public class BookBorrowDataManager implements DataManager {

    private final KeyValueDatabaseInstance instance;
    public static final String COLLECTION_NAME = "bookBorrow";

    @Override
    public void add() throws IOException {
        BookBorrow.BookBorrowBuilder bookBorrowBuilder = BookBorrow.builder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Wypełnij dane dodawanego wypożyczenia");
        System.out.print("Podaj identyfikator bibliotekarza: ");
        val librarianUUID = reader.readLine();
        val librarian = getLibrarianById(librarianUUID);
        if(librarian.isEmpty()) {
            return;
        }
        System.out.print("Podaj identyfikator wypożyczającego: ");
        val borrowerID = reader.readLine();
        val borrower = getClientByID(borrowerID);
        if(borrower.isEmpty()) {
            return;
        }
        System.out.print("Podaj nazwę ksiązki: ");
        String bookName = reader.readLine();
        System.out.print("Podaj typ operacji(W/O): ");
        String operationTypeString = reader.readLine();
        OperationKind operationKind = null;
        if("W".equals(operationTypeString)) {
            operationKind = OperationKind.BORROW;
        }
        else if("O".equals(operationTypeString)) {
            operationKind = OperationKind.RETURN;
        }
        else {
            return;
        }
        System.out.print("Podaj datę operacji: ");
        val dateString = reader.readLine();
        LocalDate date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
           date = dateFormat.parse(dateString).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            System.out.println("Podano datę w nieoczekiwanym formacie: " + dateString + " Oczekiwano: " + "yyyy-MM-dd");
            return;
        }
        var borrowsCount = instance.getObjectCount(bookName);
        String uuid = bookName + borrowsCount;
        bookBorrowBuilder._id(uuid);
        bookBorrowBuilder.librarian(librarian.get());
        bookBorrowBuilder.client(borrower.get());
        bookBorrowBuilder.bookName(bookName);
        bookBorrowBuilder.kind(operationKind);
        bookBorrowBuilder.date(date);
        var bookBorrow = bookBorrowBuilder.build();
        borrowsCount++;
        instance.updateObjectCount(bookName, borrowsCount);
        instance.setValue(COLLECTION_NAME, uuid, bookBorrow);
        System.out.println("Dodano wypożyczenie o UUID " + uuid);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja danych wypożyczenia");
        System.out.print("Podaj identyfikator aktualizowanego wypożyczenia: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey(COLLECTION_NAME, identifier)) {
            System.out.println("Nie znaleziono wypożyczenia o uuid " + identifier);
            return;
        }
        val oldBorrow = instance.getValue(COLLECTION_NAME, identifier, BookBorrow.class);
        System.out.print("Podaj identyfikator bibliotekarza("+oldBorrow.getLibrarian().getName() + " " + oldBorrow.getLibrarian().getSurname()+"):");
        val librarianUUID = reader.readLine();
        if(!librarianUUID.isBlank()) {
            val librarian = getLibrarianById(librarianUUID);
            if(librarian.isEmpty()) {
                return;
            }
            oldBorrow.setLibrarian(librarian.get());
        }
        System.out.print("Podaj identyfikator wypożyczającego("+oldBorrow.getClient().getName()+ " " + oldBorrow.getClient().getSurname() + "): ");
        val borrowerUUID = reader.readLine();
        if(!borrowerUUID.isBlank()) {
            val borrower = getClientByID(borrowerUUID);
            if(borrower.isEmpty()) {
                return;
            }
            oldBorrow.setClient(borrower.get());
        }
        System.out.print("Podaj datę dostawy ("+oldBorrow.getDate()+"): ");
        val arrivalDate = reader.readLine();
        if(!arrivalDate.isBlank()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                oldBorrow.setDate(dateFormat.parse(arrivalDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } catch (ParseException e) {
                System.out.println("Podano datę w nieoczekiwanym formacie: " + arrivalDate + " Oczekiwano: " + "yyyy-MM-dd");
                return;
            }
        }
        System.out.print("Podaj typ operacji(W/O): ("+oldBorrow.getKind().name()+"):");
        String operationTypeString = reader.readLine();
        if("W".equals(operationTypeString)) {
            oldBorrow.setKind(OperationKind.BORROW);
        }
        else if("O".equals(operationTypeString)) {
            oldBorrow.setKind(OperationKind.RETURN);
        }
        else {
            return;
        }
        System.out.print("Podaj nazwę książki:("+oldBorrow.getBookName()+"): ");
        var productName = reader.readLine();
        if(!productName.isBlank()) {
            oldBorrow.setBookName(reader.readLine());
        }
        System.out.println("Zaktualizowano kurs o UUID " + identifier);
        instance.updateValue(COLLECTION_NAME, identifier, oldBorrow);
    }

    @Override
    public void remove() throws IOException {
        System.out.println("Usuwanie wypożyczenia ");
        System.out.print("Podaj identyfikator wypożyczenia: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        instance.removeValue(COLLECTION_NAME, identifier);
        System.out.println("Kurs o UUID " + identifier + " został usunięty");
    }

    private Optional<Librarian> getLibrarianById(String uuid) {
        if(!instance.containsKey(LibrarianDataManager.COLLECTION_NAME, uuid)) {
            System.out.println("Nie znaleziono bibliotekarza o podanym uuid: " + uuid);
            return Optional.empty();
        }
        return Optional.of(instance.getValue(LibrarianDataManager.COLLECTION_NAME, uuid, Librarian.class));
    }

    private Optional<Client> getClientByID(String uuid) {
        if(!instance.containsKey(ClientDataManager.COLLECTION_NAME, uuid)) {
            System.out.println("Nie znaleziono klienta o podanym uuid: " + uuid);
            return Optional.empty();
        }
        return Optional.of( instance.getValue(ClientDataManager.COLLECTION_NAME, uuid, Client.class));
    }

    @Override
    public void show() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj identyfikator wypożyczenia: ");
        var identifier = reader.readLine();
        if(!instance.containsKey(COLLECTION_NAME, identifier)) {
            System.out.println("W bazie nie istnieje obiekt o identyfikatorze " + identifier);
            return;
        }
        System.out.println("Pobrano obiekt: ");
        BookBorrow bookBorrow = instance.getValue(COLLECTION_NAME, identifier, BookBorrow.class);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Bibliotekarz: ");
        System.out.println("\tImię: " + bookBorrow.getLibrarian().getName());
        System.out.println("\tNazwisko: " + bookBorrow.getLibrarian().getSurname());
        System.out.println("\tData zatrudnienia: " + bookBorrow.getLibrarian().getDateOfEmployment().format(DateTimeFormatter.ISO_DATE));
        System.out.println("Wypożyczający: ");
        System.out.println("\tImię: " + bookBorrow.getClient().getName());
        System.out.println("\tNazwisko: " + bookBorrow.getClient().getSurname());
        System.out.println("\tMiasto: " + bookBorrow.getClient().getAge());
        System.out.println("Nazwa książki: " + bookBorrow.getBookName());
        System.out.println("Data operacji: " + bookBorrow.getDate().format(DateTimeFormatter.ISO_DATE));
    }
}