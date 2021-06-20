package application.manager;

import application.model.PoliceStation;
import application.service.PoliceStationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PoliceStationDataManager implements DataManager {

    private final PoliceStationService policeStationService;

    public PoliceStationDataManager(PoliceStationService policeStationService) {
        this.policeStationService = policeStationService;
    }

    @Override
    public void addOrUpdate() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id komisariatu: ");
        var id = reader.readLine();
        System.out.print("Podaj nazwę komisariatu: ");
        var name = reader.readLine();
        System.out.print("Podaj miasto komisariatu: ");
        var city = reader.readLine();
        var policeStation = new PoliceStation();
        if(!id.isEmpty()) {
            policeStation.setId(Long.parseLong(id));
        }
        policeStation.setName(name);
        policeStation.setCity(city);
        policeStationService.createOrUpdate(policeStation, id);
        System.out.println("Dodano komisariat o id " + policeStation.getId());

    }

    @Override
    public void delete() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.println("Podaj id komisariatu");
        var id = reader.readLine();
        policeStationService.delete(id);
        System.out.println("Komisariat o id " + id + " został usunięty");
    }

    @Override
    public void show() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id komisariatu");
        var id = reader.readLine();
        var policeStation = policeStationService.read(id);
        if(policeStation == null) {
            return;
        }
        System.out.println("Nazwa komisariatu: " + policeStation.getName());
        System.out.println("Miasto komisariatu: " + policeStation.getCity());
    }
}