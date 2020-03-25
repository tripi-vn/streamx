package vn.vntravel.replication;

import vn.vntravel.util.StoppableTask;

public interface Replicator extends StoppableTask {
    void startReplicator() throws Exception;
    Long getLastHeartbeatRead();

    void stopAtHeartbeat(long heartbeat);
    void runLoop() throws Exception;
}
