package datamanager;

import database.KeyValueDatabaseInstance;

public class DataManagerFactory {

    public static DataManager getDataManager(int objectType, KeyValueDatabaseInstance hazelcastInstance) {
        switch(objectType) {
            case 1:
                return new LibrarianDataManager(hazelcastInstance);
            case 2:
                return new ClientDataManager(hazelcastInstance);
            case 3:
                return new BookBorrowDataManager(hazelcastInstance);
        }
        throw new RuntimeException();
    }
}
