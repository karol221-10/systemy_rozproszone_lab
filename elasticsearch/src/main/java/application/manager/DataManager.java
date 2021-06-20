package application.manager;

import java.io.IOException;

public interface DataManager {
    void addOrUpdate() throws IOException;
    void delete() throws IOException;
    void show() throws IOException;
}
