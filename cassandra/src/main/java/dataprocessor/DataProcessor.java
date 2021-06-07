package dataprocessor;

import database.KeyValueDatabaseInstance;
import database.KeyValueDatabaseProcessor;
import datamanager.TouristDataManager;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Tourist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


@RequiredArgsConstructor
public class DataProcessor {

    private final KeyValueDatabaseProcessor instance;
    private final KeyValueDatabaseInstance dataAccessor;

    public void getByField() throws IOException {
        System.out.println("Pobieram wszystkich turystów po właściwości");
        System.out.print("Podaj nazwę pola: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        val fieldName = reader.readLine();
        System.out.print("Podaj wartość pola: ");
        val fieldValue = reader.readLine();
        System.out.println("Pokazuję wszystkich turystów, dla których " + fieldName + " = " + fieldValue);
        val result = dataAccessor.findBy(TouristDataManager.COLLECTION_NAME, fieldName, fieldValue, Tourist.class);
        result.forEach(tourist -> {
            System.out.println("Id: " + tourist.getId());
            System.out.println("Imię: " + tourist.getName());
            System.out.println("Nazwisko: " + tourist.getSurname());
            System.out.println("Wiek: " + tourist.getAge());
        });
    }

    public void countAverageAgeOfClients() {
        System.out.println("Średni wiek zarejestrowanych turystów: " + instance.calcAvgByField(TouristDataManager.COLLECTION_NAME, "age", Tourist.class));
    }
}
