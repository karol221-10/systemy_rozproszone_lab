package application.manager;

import application.service.PoliceStationService;
import application.service.PolicemanService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PoliceStationCrewManager {

    private final PolicemanService policemanService;
    private final PoliceStationService policeStationService;

    public PoliceStationCrewManager(
            PolicemanService policemanService,
            PoliceStationService policeStationService) {
        this.policemanService = policemanService;
        this.policeStationService = policeStationService;
    }

    public void addPolicemanToStation() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id policjanta: ");
        var policeId = reader.readLine();
        System.out.print("Podaj id komisariatu: ");
        var policeStationId = reader.readLine();
        var policeMan = policemanService.read(policeId);
        var policeStation = policeStationService.read(policeStationId);
        if( policeMan == null) {
            System.out.println("Policjant o id " + policeId + " nie istnieje");
            return;
        }
        if( policeStation == null) {
            System.out.println("Komisariat o id " + policeStationId + " nie istnieje");
            return;
        }
        policeStation.addPoliceman(policeMan);
        policeStationService.createOrUpdate(policeStation, policeStationId);
        System.out.println("Dodano policjanta " + policeMan.getName() + " " + policeMan.getSurname() + " do komisariatu " + policeStation.getName());
    }
}
