package application.model;


import java.util.HashSet;
import java.util.Set;

public class PoliceStation {

    private Long id;

    private String name;

    private String city;

    private Set<Policeman> policemans = new HashSet<>();

    public void addPoliceman(Policeman policeman) {
        policemans.add(policeman);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Policeman> getPolicemans() {
        return policemans;
    }

    public void setPolicemans(Set<Policeman> policemans) {
        this.policemans = policemans;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
