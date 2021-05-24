package datamanager;

import com.hazelcast.core.HazelcastInstance;
import database.KeyValueDatabaseInstance;
import lombok.RequiredArgsConstructor;
import lombok.val;
import model.Client;
import model.Courier;
import model.Course;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class CourseDataManager implements DataManager {

    private final KeyValueDatabaseInstance instance;

    @Override
    public void add() throws IOException {
        Course.CourseBuilder courseBuilder = Course.builder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Wypełnij dane dodawanego kursu");
        System.out.print("Podaj identyfikator nadawcy: ");
        val senderUUID = reader.readLine();
        val sender = getClientByUUID(senderUUID);
        if(sender.isEmpty()) {
            return;
        }
        System.out.print("Podaj identyfikator odbiorcy: ");
        val receiverUUID = reader.readLine();
        val receiver = getClientByUUID(receiverUUID);
        if(receiver.isEmpty()) {
            return;
        }
        System.out.print("Podaj identyfikator kuriera: ");
        val courierUUID = reader.readLine();
        val courier = getCourierByUUID(courierUUID);
        if(courier.isEmpty()) {
            return;
        }
        System.out.print("Podaj datę dostawy: ");
        val arrivalDate = reader.readLine();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            courseBuilder.arrivalDate(dateFormat.parse(arrivalDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } catch (ParseException e) {
            System.out.println("Podano datę w nieoczekiwanym formacie: " + arrivalDate + " Oczekiwano: " + "yyyy-MM-dd");
            return;
        }
        System.out.print("Podaj nazwę produktu: ");
        courseBuilder.productName(reader.readLine());
        courseBuilder.sourceClient(sender.get());
        courseBuilder.destinationClient(receiver.get());
        courseBuilder.courier(courier.get());
        String uuidPrefix = receiver.get().getCity()+"_"+sender.get().getCity();
        var coursesCount = (Integer) Optional.ofNullable(instance.getValue("count",uuidPrefix)).orElse(0);
        courseBuilder.courseId(uuidPrefix + coursesCount);
        var course = courseBuilder.build();
        coursesCount++;
        var uuid = uuidPrefix + coursesCount;
        instance.setValue("count", uuidPrefix, coursesCount);
        instance.setValue("courses", uuid, course);
        System.out.println("Dodano kurs o UUID " + uuid);
    }

    @Override
    public void update() throws IOException {
        System.out.println("Aktualizacja danych kursu");
        System.out.print("Podaj identyfikator aktualizowanego kursu: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        if(!instance.containsKey("courses", identifier)) {
            System.out.println("Nie znaleziono kursu o uuid " + identifier);
            return;
        }
        val oldCourse = (Course)instance.getValue("courses", identifier);
        System.out.print("Podaj identyfikator nadawcy("+oldCourse.getSourceClient().getName() + " " + oldCourse.getSourceClient().getSurname()+"):");
        val senderUUID = reader.readLine();
        if(!senderUUID.isBlank()) {
            val sender = getClientByUUID(senderUUID);
            if(sender.isEmpty()) {
                return;
            }
            oldCourse.setSourceClient(sender.get());
        }
        System.out.print("Podaj identyfikator odbiorcy("+oldCourse.getDestinationClient().getName() + " " + oldCourse.getDestinationClient().getSurname()+"): ");
        val receiverUUID = reader.readLine();
        if(!receiverUUID.isBlank()) {
            val receiver = getClientByUUID(receiverUUID);
            if(receiver.isEmpty()) {
                return;
            }
            oldCourse.setDestinationClient(receiver.get());
        }
        System.out.print("Podaj identyfikator kuriera ("+oldCourse.getCourier().getName() + " " + oldCourse.getCourier().getSurname()+"): ");
        val courierUUID = reader.readLine();
        if (!courierUUID.isBlank()) {
            val courier = getCourierByUUID(courierUUID);
            if(courier.isEmpty()) {
                return;
            }
            oldCourse.setCourier(courier.get());
        }
        System.out.print("Podaj datę dostawy ("+oldCourse.getArrivalDate()+"): ");
        val arrivalDate = reader.readLine();
        if(!arrivalDate.isBlank()) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                oldCourse.setArrivalDate(dateFormat.parse(arrivalDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            } catch (ParseException e) {
                System.out.println("Podano datę w nieoczekiwanym formacie: " + arrivalDate + " Oczekiwano: " + "yyyy-MM-dd");
                return;
            }
        }
        System.out.print("Podaj nazwę produktu:("+oldCourse.getProductName()+"): ");
        var productName = reader.readLine();
        if(!productName.isBlank()) {
            oldCourse.setProductName(reader.readLine());
        }
        System.out.println("Zaktualizowano kurs o UUID " + identifier);
        instance.setValue("courses", identifier, oldCourse);
    }

    @Override
    public void remove() throws IOException {
        System.out.println("Usuwanie kursu ");
        System.out.print("Podaj identyfikator kursu: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        var identifier = reader.readLine();
        instance.removeValue("courses", identifier);
        System.out.println("Kurs o UUID " + identifier + " został usunięty");
    }

    private Optional<Client> getClientByUUID(String uuid) {
        if(!instance.containsKey("clients", uuid)) {
            System.out.println("Nie znaleziono klienta o podanym uuid: " + uuid);
            return Optional.empty();
        }
        return Optional.of((Client)instance.getValue("clients", uuid));
    }

    private Optional<Courier> getCourierByUUID(String uuid) {
        if(!instance.containsKey("couriers", uuid)) {
            System.out.println("Nie znaleziono kuriera o podanym uuid: " + uuid);
            return Optional.empty();
        }
        return Optional.of((Courier) instance.getValue("couriers", uuid));
    }

    @Override
    public void show() throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Podaj identyfikator kursu: ");
        var identifier = reader.readLine();
        if(!instance.containsKey("courses", identifier)) {
            System.out.println("W bazie nie istnieje obiekt o identyfikatorze " + identifier);
            return;
        }
        System.out.println("Pobrano obiekt: ");
        Course course = (Course)instance.getValue("courses", identifier);
        System.out.println("Identyfikator: " + identifier);
        System.out.println("Nadawca: ");
        System.out.println("\tImię: " + course.getSourceClient().getName());
        System.out.println("\tNazwisko: " + course.getSourceClient().getSurname());
        System.out.println("\tMiasto: " + course.getSourceClient().getCity());
        System.out.println("Odbiorca: ");
        System.out.println("\tImię: " + course.getDestinationClient().getName());
        System.out.println("\tNazwisko: " + course.getDestinationClient().getSurname());
        System.out.println("\tMiasto: " + course.getDestinationClient().getCity());
        System.out.println("Kurier: ");
        System.out.println("\tImię: " + course.getCourier().getName());
        System.out.println("\tNazwisko: " + course.getCourier().getSurname());
        System.out.println("\tWiek: " + course.getCourier().getAge());
        System.out.println("Nazwa towaru: " + course.getProductName());
        System.out.println("Data dostawy: " + course.getArrivalDate().format(DateTimeFormatter.ISO_DATE));
    }
}