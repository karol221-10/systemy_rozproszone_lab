package application.service;

import application.model.Policeman;
import org.elasticsearch.client.RestHighLevelClient;

public class PolicemanService extends GenericService <Policeman> {

    public PolicemanService(RestHighLevelClient session) {
        super(session);
    }

    @Override
    Class<Policeman> getEntityType() {
        return Policeman.class;
    }
}
