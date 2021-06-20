package application.manager;

import application.model.Policeman;
import application.service.PoliceStationService;
import application.service.PolicemanService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PolicemanDataManager implements DataManager {

    private final PolicemanService policemanService;
    private final PoliceStationService policeStationService;

    public PolicemanDataManager(PolicemanService policemanService,
                                PoliceStationService policeStationService) {
        this.policemanService = policemanService;
        this.policeStationService = policeStationService;
    }

    @Override
    public void addOrUpdate() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id:");
        var id = reader.readLine();
        System.out.print("Podaj imię policjanta: ");
        var name = reader.readLine();
        System.out.print("Podaj nazwisko policjanta: ");
        var surname = reader.readLine();
        Policeman policeman = new Policeman();
        policeman.setName(name);
        policeman.setSurname(surname);
        policemanService.createOrUpdate(policeman, id);
    }

    @Override
    public void delete() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id: ");
        var id = reader.readLine();
        policemanService.delete(id);
    }

    @Override
    public void show() throws IOException {
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        System.out.print("Podaj id policjanta");
        var id = reader.readLine();
        var policeman = policemanService.read(id);
        if(policeman == null) return;
        System.out.println("Imię policjanta: " + policeman.getName());
        System.out.println("Nazwisko policjanta: " + policeman.getSurname());
    }
}
