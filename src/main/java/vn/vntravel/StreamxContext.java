package vn.vntravel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.replication.HeartbeatNotifier;
import vn.vntravel.replication.Replicator;
import vn.vntravel.util.StoppableTask;
import vn.vntravel.util.TaskManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class StreamxContext {
    static final Logger LOGGER = LoggerFactory.getLogger(StreamxContext.class);

    private final StreamxConfig config;
    private Replicator replicator;
    private Thread terminationThread;

    private volatile Exception error;

    private final HeartbeatNotifier heartbeatNotifier;
    private final TaskManager taskManager;

    public StreamxContext(StreamxConfig config) throws SQLException, URISyntaxException {
        this.config = config;
        this.taskManager = new TaskManager();
        this.heartbeatNotifier = new HeartbeatNotifier();
    }

    public StreamxConfig getConfig() {
        return this.config;
    }

    public void start() throws IOException {
        // for monitor
    }

    public void addTask(StoppableTask task) {
        this.taskManager.add(task);
    }

    public Thread terminate() {
        return terminate(null);
    }

    public HeartbeatNotifier getHeartbeatNotifier() {
        return heartbeatNotifier;
    }

    public Thread terminate(Exception error) {
        if (this.error == null) {
            this.error = error;
        }

        if (taskManager.requestStop()) {
            if (this.error == null && this.replicator != null) {
                sendFinalHeartbeat();
            }
            this.terminationThread = spawnTerminateThread();
        }
        return this.terminationThread;
    }

    private Thread spawnTerminateThread() {
        // Because terminate() may be called from a task thread
        // which won't end until we let its event loop progress,
        // we need to perform termination in a new thread
        final AtomicBoolean shutdownComplete = new AtomicBoolean(false);
        final StreamxContext self = this;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Spawn an inner thread to perform shutdown
                final Thread shutdownThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        self.shutdown(shutdownComplete);
                    }
                }, "shutdownThread");
                shutdownThread.start();

                // wait for its completion, timing out after 10s
                try {
                    shutdownThread.join(10000L);
                } catch (InterruptedException e) {
                    // ignore
                }

                LOGGER.debug("Shutdown complete: " + shutdownComplete.get());
                if (!shutdownComplete.get()) {
                    LOGGER.error("Shutdown stalled - forcefully killing maxwell process");
                    if (self.error != null) {
                        LOGGER.error("Termination reason:", self.error);
                    }
                    Runtime.getRuntime().halt(1);
                }
            }
        }, "shutdownMonitor");
        thread.setDaemon(false);
        thread.start();
        return thread;
    }

    public void setReplicator(Replicator replicator) {
        this.addTask(replicator);
        this.replicator = replicator;
    }

    private void sendFinalHeartbeat() {
        long heartbeat = System.currentTimeMillis();
        LOGGER.info("Sending final heartbeat: " + heartbeat);
        try {
            this.replicator.stopAtHeartbeat(heartbeat);
            long deadline = heartbeat + 5000L;
        } catch (Exception e) {
            LOGGER.error("Failed to send final heartbeat", e);
        }
    }

    private void shutdown(AtomicBoolean complete) {
        try {
            taskManager.stop(this.error);
            complete.set(true);
        } catch (Exception e) {
            LOGGER.error("Exception occurred during shutdown:", e);
        }
    }

    public Exception getError() {
        return error;
    }
}
