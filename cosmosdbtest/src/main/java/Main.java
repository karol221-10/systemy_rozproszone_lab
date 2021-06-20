import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosContainerProperties;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.CosmosDatabaseResponse;
import com.azure.cosmos.models.ThroughputProperties;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Main {


    /**
     * Run a Hello CosmosDB console application.
     * <p>
     * This is a simple sample application intended to demonstrate Create, Read, Update, Delete (CRUD) operations
     * with Azure Cosmos DB Java SDK, as applied to databases, containers and items. This sample will
     * 1. Create synchronous client, database and container instances
     * 2. Create several items
     * 3. Upsert one of the items
     * 4. Perform a query over the items
     * 5. Delete an item
     * 6. Delete the Cosmos DB database and container resources and close the client.     *
     */
    //  <Main>
    public static void main(String[] args) throws Exception {

        Processor processor = new Processor();
        processor.init();

        while(1==1) {
            System.out.println("Wybierz rodzaj operacji");
            System.out.println("[1] Dodaj turystę");
            System.out.println("[2] Edytuj turystę");
            System.out.println("[3] Usuń turystę");
            System.out.println("[4] Pobierz po id");
            System.out.println("[5] Pobierz po nazwisku");
            System.out.println("[6] Oblicz średni wiek dodanych turystów");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            var line = reader.readLine();
            if ("1".equals(line)) {
                System.out.print("Podaj id turysty: ");
                var id = reader.readLine();
                System.out.print("Podaj imię turysty: ");
                var name = reader.readLine();
                System.out.print("Podaj nazwisko turysty: ");
                var surname = reader.readLine();
                System.out.print("Podaj wiek turysty: ");
                var age = Integer.parseInt(reader.readLine());
                Tourist tourist = new Tourist();
                tourist.setId(id);
                tourist.setName(name);
                tourist.setSurname(surname);
                tourist.setAge(age);
                processor.addTourist(tourist);
            }
            if ("2".equals(line)) {
                System.out.print("Podaj id turysty: ");
                var id = reader.readLine();
                System.out.print("Podaj imię turysty: ");
                var name = reader.readLine();
                System.out.print("Podaj nazwisko turysty: ");
                var surname = reader.readLine();
                System.out.print("Podaj wiek turysty: ");
                var age = Integer.parseInt(reader.readLine());
                Tourist tourist = new Tourist();
                tourist.setId(id);
                tourist.setName(name);
                tourist.setSurname(surname);
                tourist.setAge(age);
                processor.updateTourist(tourist);
            }
            if("3".equals(line)) {
                System.out.print("Podaj id turysty: ");
                var id = reader.readLine();
                processor.deleteTouristById(id);
            }
            if ("5".equals(line)) {
                System.out.print("Podaj nazwisko: ");
                var surname = reader.readLine();
                var result = processor.getBySurname(surname);
                System.out.println(result);
            }
            if("4".equals(line)) {
                System.out.println("Podaj id turysty");
                var id = reader.readLine();
                var tourist = processor.getById(id);
                System.out.println(tourist);
            }
            if("6".equals(line)) {
                var result = processor.calculateAvgAge();
                System.out.println(result);
            }
        }
    }

    //  </Main>

}
