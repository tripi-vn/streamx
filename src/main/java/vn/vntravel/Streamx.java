package vn.vntravel;

import com.djdch.log4j.StaticShutdownCallbackRegistry;
import org.slf4j.LoggerFactory;
import vn.vntravel.consumer.AbstractConsumer;
import vn.vntravel.replication.BrokerConnectorReplicator;
import vn.vntravel.replication.Position;
import vn.vntravel.replication.Replicator;
import vn.vntravel.util.Logging;
import org.slf4j.Logger;

import java.net.URISyntaxException;
import java.sql.SQLException;

public class Streamx implements Runnable {

    protected StreamxConfig config;
    protected StreamxContext context;
    protected Replicator replicator;

    static final Logger LOGGER = LoggerFactory.getLogger(Streamx.class);

    public Streamx(StreamxConfig config) throws SQLException, URISyntaxException {
        this(new StreamxContext(config));
    }

    protected Streamx(StreamxContext context) throws SQLException, URISyntaxException {
        this.config = context.getConfig();
        this.context = context;
//        this.context.probeConnections();
    }

    @Override
    public void run() {
        try {
            start();
        } catch (Exception e) {
            LOGGER.error("maxwell encountered an exception", e);
        }
    }

    private void start() throws Exception {
        try {
            startInner();
        } catch ( Exception e) {
            this.context.terminate(e);
        } finally {
//            onReplicatorEnd();
            this.terminate();
        }

        Exception error = this.context.getError();
        if (error != null) {
            throw error;
        }
    }

    protected void onReplicatorStart() {}
    protected void onReplicatorEnd() {}

    static String bootString = "Streamx v%s is booting (%s), starting at %s";
    private void logBanner(AbstractConsumer consumer, Position initialPosition) {
        String consumerName = consumer.getClass().getSimpleName();
        LOGGER.info(String.format(bootString, getStreamxVersion(), consumerName, initialPosition.toString()));
    }

    public String getStreamxVersion() {
        String packageVersion = getClass().getPackage().getImplementationVersion();
        if ( packageVersion == null )
            return "??";
        else
            return packageVersion;
    }

    private void startInner() throws Exception {
        AbstractConsumer consumer = this.context.getConsumer();

        this.replicator = new BrokerConnectorReplicator(config.clientID,
                consumer,
                context.getHeartbeatNotifier());

        context.setReplicator(replicator);
        this.context.start();
        this.onReplicatorStart();

        replicator.runLoop();
    }

    public void terminate() {
        Thread terminationThread = this.context.terminate();
        if (terminationThread != null) {
            try {
                terminationThread.join();
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }

    public static void main(String[] args) {
        try {
            org.apache.log4j.BasicConfigurator.configure();
            Logging.setupLogBridging();
            args = new String[]{"--config=/home/lion/streamx/config.properties"};

            StreamxConfig config = new StreamxConfig(args);
            if ( config.log_level != null )
                Logging.setLevel(config.log_level);

            final Streamx streamx = new Streamx(config);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                streamx.terminate();
                StaticShutdownCallbackRegistry.invoke();
            }));

            streamx.start();
        } catch ( SQLException e ) {
            // catch SQLException explicitly because we likely don't care about the stacktrace
            LOGGER.error("SQLException: " + e.getLocalizedMessage());
            System.exit(1);
        } catch ( URISyntaxException e ) {
            // catch URISyntaxException explicitly as well to provide more information to the user
            LOGGER.error("Syntax issue with URI, check for misconfigured host, port, database, or JDBC options (see RFC 2396)");
            LOGGER.error("URISyntaxException: " + e.getLocalizedMessage());
            System.exit(1);
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
