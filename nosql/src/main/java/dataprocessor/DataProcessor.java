package dataprocessor;

import database.KeyValueDatabaseInstance;
import database.KeyValueDatabaseProcessor;
import datamanager.BookBorrowDataManager;
import datamanager.ClientDataManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Client;
import model.Librarian;
import model.BookBorrow;
import model.OperationKind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class DataProcessor {

    private final KeyValueDatabaseProcessor instance;
    private final KeyValueDatabaseInstance dataAccessor;

    public void showAllOperationByKind() throws IOException {
        System.out.println("Pokazuję wszystkie operacje o typie: ");
        System.out.print("Podaj typ operacji (W/O): ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var operationType = reader.readLine();
        String operationTypeString;
        if(operationType.equals("W")) {
            operationTypeString = OperationKind.BORROW.name();
        }
        else {
            operationTypeString = OperationKind.RETURN.name();
        }
        var borrows = dataAccessor.findBy(BookBorrowDataManager.COLLECTION_NAME, "kind", operationTypeString, BookBorrow.class);
        borrows.forEach(borrow -> {
            System.out.println("Klient: " + borrow.getClient().getName() + " " + borrow.getClient().getSurname() + " " + borrow.getClient().getAge());
            System.out.println("Bibliotekarz: " + borrow.getLibrarian().getName() + " " + borrow.getLibrarian().getSurname());
            System.out.println("Tytuł książki: " + borrow.getBookName());
            System.out.println("Data wypożyczenia: " + borrow.getDate().format(DateTimeFormatter.ISO_DATE));
        });
    }

    public void countAverageAgeOfClients() {
        System.out.println("Średni wiek zarejestrowanych klientów: " + instance.calcAvgByField(ClientDataManager.COLLECTION_NAME, "age", Client.class));
    }
}
