package datamanager;

import com.hazelcast.core.HazelcastInstance;

import java.io.IOException;

public interface DataManager {
    void add() throws IOException;
    void update() throws IOException;
    void remove() throws IOException;
    void show() throws IOException;
}
