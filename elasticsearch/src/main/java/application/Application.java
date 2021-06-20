package application;

import application.manager.PoliceStationCrewManager;
import application.manager.PoliceStationDataManager;
import application.manager.PolicemanDataManager;
import application.service.PoliceStationService;
import application.service.PolicemanService;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Application {

    public static void main(String[] args) throws IOException {
        RestHighLevelClient session = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
        //   session.purgeDatabase();
        PolicemanService policemanService = new PolicemanService(session);
        PoliceStationService policeStationService = new PoliceStationService(session);
        PolicemanDataManager policemanDataManager = new PolicemanDataManager(policemanService, policeStationService);
        PoliceStationDataManager policeStationDataManager = new PoliceStationDataManager(policeStationService);
        PoliceStationCrewManager policeStationCrewManager = new PoliceStationCrewManager(policemanService, policeStationService);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        while(true) {
            System.out.println("Wybierz operację");
            System.out.println("[1] Dodawanie/aktualizacja danych");
            System.out.println("[2] Zarządzanie załogą");
            System.out.println("[3] Usuwanie danych");
            System.out.println("[4] Pobieranie danych");
            System.out.println("[5] Pobierz wszystkie komisariaty z miasta");
            System.out.println("[6] Zlicz policjantów wg komisariatów");

            int selector = Integer.parseInt(reader.readLine());

            switch(selector) {
                case 1:
                {
                    var dataType = selectDataType();
                    if (dataType == 1) {
                        policemanDataManager.addOrUpdate();;
                    }
                    if (dataType == 2) {
                        policeStationDataManager.addOrUpdate();
                    }
                }
                break;
                case 2:
                {
                    var dataType = selectOperationType();
                    if (dataType == 1) {
                        policeStationCrewManager.addPolicemanToStation();
                    }
                }
                break;
                case 3: {
                    var dataType = selectDataType();
                    if (dataType == 1) {
                        policemanDataManager.delete();
                    }
                }
                break;
                case 4:
                {
                    var dataType = selectDataType();
                    if(dataType == 1) {
                        policemanDataManager.show();
                    }
                }
                break;
                case 5: {
                    System.out.print("Podaj nazwę miasta: ");
                    var townName = reader.readLine();
                    var result = policeStationService.getAllPoliceStationsFromTown(townName);
                    result.forEach(policeStation -> {
                        System.out.println("Id komisariatu: " + policeStation.getId());
                        System.out.println("Nazwa komisariatu: " + policeStation.getName());
                        System.out.println("Miasto komisariatu: " + policeStation.getCity());
                    });
                }
                break;
                case 6: {
                    var result = policeStationService.countPolicemansByCity();
                    System.out.println(result);
                }
            }
        }

    }

    private static final int selectDataType() throws IOException {
        System.out.println("Wybierz typ danych: ");
        System.out.println("[1] Policjant");
        System.out.println("[2] Komisariat");
        System.out.print("Wybierz opcję: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }

    private static final int selectOperationType() throws IOException {
        System.out.println("Wybierz typ operacji");
        System.out.println("[1] Dodaj policjanta do komisariatu");
        System.out.println("[2] Usuń policjanta z komisariatu");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }
}
