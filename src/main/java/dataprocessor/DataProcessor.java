package dataprocessor;

import com.hazelcast.aggregation.Aggregators;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import database.KeyValueDatabaseInstance;
import database.KeyValueDatabaseProcessor;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Client;
import model.Courier;
import model.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class DataProcessor {

    private final KeyValueDatabaseProcessor instance;
    private final KeyValueDatabaseInstance dataAccessor;

    public void showAvgAgeOfCouriers() {
        System.out.println("Sredni wiek zarejestrowanych kurierów: " + instance.calcAvgByField("couriers", "age"));
    }

    public void showAllClientsFromTown() throws IOException {
        System.out.println("Pokazuję wszystkich klientów z miasta: ");
        System.out.print("Podaj nazwę miasta: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var townName = reader.readLine();

        dataAccessor.<Client>getAll("clients").stream()
                .filter(entrySet ->  entrySet.getCity().equals(townName))
                .forEach(foundClient -> {
                    System.out.println("Imię klienta: " + foundClient.getName());
                    System.out.println("Nazwisko klienta: " + foundClient.getSurname());
                });
    }

    public void avgNumberOfCoursesForOneCourier() {
        val courses = dataAccessor.<Course>getAll("courses")
                .stream()
                .collect(Collectors.groupingBy(Course::getCourier, Collectors.counting()));
        courses.forEach((courier, value) -> System.out.println("Kurier " + courier.getName() + " " + courier.getSurname() + " = " + value + " kursów"));
    }
}
