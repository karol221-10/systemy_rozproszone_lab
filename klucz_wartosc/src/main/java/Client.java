import database.KeyValueDatabaseInstance;
import database.KeyValueDatabaseProcessor;
import datamanager.DataManagerFactory;
import dataprocessor.DataProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Client {

    public static void process(KeyValueDatabaseInstance instance, KeyValueDatabaseProcessor processor) throws IOException {
        while(true) {
            System.out.println("Wybierz rodzaj operacji: ");
            System.out.println("[1] Dodaj dane");
            System.out.println("[2] Aktualizuj dane");
            System.out.println("[3] Usuń dane");
            System.out.println("[4] Pobierz dane");
            System.out.println("[5] Przetwórz dane");
            System.out.println("[6] Pobierz klientów z miasta");
            System.out.print("Wybierz opcję: ");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));
            int selector = Integer.parseInt(reader.readLine());
            switch(selector) {
                case 1: {
                    int selectedDataType = selectDataType();
                    var dataManager = DataManagerFactory.getDataManager(selectedDataType, instance);
                    dataManager.add();
                    break;
                }
                case 2: {
                    int selectedDataType = selectDataType();
                    var dataManager = DataManagerFactory.getDataManager(selectedDataType, instance);
                    dataManager.update();
                    break;
                }
                case 3: {
                    int selectedDataType = selectDataType();
                    var dataManager = DataManagerFactory.getDataManager(selectedDataType, instance);
                    dataManager.remove();
                    break;
                }
                case 4: {
                    int selectedDataType = selectDataType();
                    var dataManager = DataManagerFactory.getDataManager(selectedDataType, instance);
                    dataManager.show();
                    break;
                }
             case 5: {
                var dataProcessor = new DataProcessor(processor, instance);
                int selectedProcessType = selectProcessDataType();
                switch(selectedProcessType) {
                    case 1:
                        dataProcessor.showAvgAgeOfCouriers();;
                        break;
                    case 2:
                        dataProcessor.avgNumberOfCoursesForOneCourier();
                        break;
                    default:
                        System.out.println("Wybrano nieprawidłową opcję");
                }
            }
            case 6: {
                var dataProcessor = new DataProcessor(processor, instance);
                dataProcessor.showAllClientsFromTown();
                break;
            }
                default:
                    System.out.println("Wybrano nieprawidłową opcję");
            }
        }
    }

    private static final int selectProcessDataType() throws IOException {
        System.out.println("Wybierz sposób przetworzenia danych");
        System.out.println("[1] Sredni wiek kurierów, którzy obsługują zlecenia");
        System.out.println("[2] Ilość kursów przypadających na jednego kuriera");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }

    private static final int selectDataType() throws IOException {
        System.out.println("Wybierz typ danych: ");
        System.out.println("[1] Kurier");
        System.out.println("[2] Klient");
        System.out.println("[3] Kurs");
        System.out.print("Wybierz opcję: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        return Integer.parseInt(reader.readLine());
    }
}
