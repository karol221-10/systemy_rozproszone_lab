import com.hazelcast.core.Hazelcast;
import lombok.val;

import java.net.UnknownHostException;

public class HazelcastServer {
    public static void main(String[] args) throws UnknownHostException {
        var config = HConfig.getConfig();
        val hazelcast = Hazelcast.newHazelcastInstance(config);
        hazelcast.getDistributedObjects()
                .forEach(distributedObject -> distributedObject.destroy());
    }
}
