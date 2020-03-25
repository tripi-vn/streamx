package vn.vntravel;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import vn.vntravel.util.AbstractConfig;
import vn.vntravel.util.StreamxOptionParser;

import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class StreamxConfig extends AbstractConfig {
    public String log_level;
    public final Properties kafkaProperties;
    public String clientID;

    public StreamxConfig() {
        kafkaProperties = new Properties();

        setup(null, null);
    }

    public StreamxConfig(String argv[]) {
        this();
        this.parse(argv);
    }


    private void setup(OptionSet options, Properties properties) {
        this.log_level = fetchOption("log_level", options, properties, null);
        this.clientID           = fetchOption("client_id", options, properties, "streamx");

        String kafkaBootstrapServers = fetchOption("kafka.bootstrap.servers", options, properties, null);
        if ( kafkaBootstrapServers != null )
            this.kafkaProperties.setProperty("bootstrap.servers", kafkaBootstrapServers);

        if ( properties != null ) {
            for (Enumeration<Object> e = properties.keys(); e.hasMoreElements(); ) {
                String k = (String) e.nextElement();
                if (k.startsWith("kafka.")) {
                    if (k.equals("kafka.bootstrap.servers") && kafkaBootstrapServers != null)
                        continue; // don't override command line bootstrap servers with config files'

                    this.kafkaProperties.setProperty(k.replace("kafka.", ""), properties.getProperty(k));
                }
            }
        }
    }

    private void parse(String [] argv) {
        StreamxOptionParser parser = buildOptionParser();
        OptionSet options = parser.parse(argv);

        Properties properties;

        if (options.has("config")) {
            properties = parseFile((String) options.valueOf("config"), true);
        } else {
            properties = parseFile(DEFAULT_CONFIG_FILE, false);
        }

        String envConfigPrefix = fetchOption("env_config_prefix", options, properties, null);

        if (envConfigPrefix != null) {
            String prefix = envConfigPrefix.toLowerCase();
            System.getenv().entrySet().stream()
                    .filter(map -> map.getKey().toLowerCase().startsWith(prefix))
                    .forEach(config -> properties.put(config.getKey().toLowerCase().replaceFirst(prefix, ""), config.getValue()));
        }

        if (options.has("help"))
            usage("Help for Streamx:", parser, (String) options.valueOf("help"));

        setup(options, properties);

        List<?> arguments = options.nonOptionArguments();
        if(!arguments.isEmpty()) {
            usage("Unknown argument(s): " + arguments);
        }
    }

    private Properties parseFile(String filename, Boolean abortOnMissing) {
        Properties p = readPropertiesFile(filename, abortOnMissing);

        if ( p == null )
            p = new Properties();

        return p;
    }

    @Override
    protected StreamxOptionParser buildOptionParser() {
        final StreamxOptionParser parser = new StreamxOptionParser();
        parser.accepts( "config", "location of config file" ).withRequiredArg();
        parser.accepts( "env_config_prefix", "prefix of env var based config, case insensitive" ).withRequiredArg();
        parser.accepts( "log_level", "log level, one of DEBUG|INFO|WARN|ERROR" ).withRequiredArg();
        parser.accepts( "daemon", "run maxwell in the background" ).withOptionalArg();

        parser.separator();
        parser.section( "kafka" );

        parser.accepts( "kafka.bootstrap.servers", "at least one kafka server, formatted as HOST:PORT[,HOST:PORT]" ).withRequiredArg();
        parser.separator();

        return parser;
    }
}
