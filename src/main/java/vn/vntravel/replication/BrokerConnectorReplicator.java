package vn.vntravel.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.util.RunLoopProcess;

public class BrokerConnectorReplicator extends RunLoopProcess implements Replicator {
    static final Logger LOGGER = LoggerFactory.getLogger(BrokerConnectorReplicator.class);
    private final String clientID;
    private final HeartbeatNotifier heartbeatNotifier;

    public BrokerConnectorReplicator(String clientID,
                                     HeartbeatNotifier heartbeatNotifier) {
        this.clientID = clientID;
        this.heartbeatNotifier = heartbeatNotifier;
    }

    @Override
    protected void work() throws Exception {

    }

    @Override
    public void startReplicator() throws Exception {

    }

    @Override
    public Long getLastHeartbeatRead() {
        return null;
    }

    @Override
    public void stopAtHeartbeat(long heartbeat) {

    }
}
