package datamanager;

import database.KeyValueDatabaseInstance;

public class DataManagerFactory {

    public static DataManager getDataManager(int objectType, KeyValueDatabaseInstance hazelcastInstance) {
        switch(objectType) {
            case 1:
                return new TouristDataManager(hazelcastInstance);
        }
        throw new RuntimeException();
    }
}
