package vn.vntravel.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.StreamxContext;
import vn.vntravel.util.StoppableTask;
import vn.vntravel.util.StoppableTaskState;

import java.util.Properties;
import java.util.concurrent.TimeoutException;

public class StreamxKafkaConsumer extends AbstractConsumer {
    private final StreamxKafkaConsumerWorker worker;
    public StreamxKafkaConsumer(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        super(context);
        this.worker = new StreamxKafkaConsumerWorker(context, kafkaProperties, kafkaTopic);
    }
}

class StreamxKafkaConsumerWorker extends AbstractAsyncConsumer implements Runnable, StoppableTask {
    static final Logger LOGGER = LoggerFactory.getLogger(StreamxKafkaConsumerWorker.class);
    private final Consumer<String, String> kafka;
    private final String topic;
    private final boolean interpolateTopic;
    private Thread thread;
    private StoppableTaskState taskState;

    public StreamxKafkaConsumerWorker(StreamxContext context, String kafkaTopic, Consumer<String,String> consumer) {
        super(context);
        this.kafka = consumer;
        if ( kafkaTopic == null ) {
            this.topic = "maxwell";
        } else {
            this.topic = kafkaTopic;
        }
        this.interpolateTopic = this.topic.contains("%");

    }

    public StreamxKafkaConsumerWorker(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        this(context, kafkaTopic, new KafkaConsumer<>(kafkaProperties, new StringDeserializer(), new StringDeserializer()));
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while ( true ) {
            try {

            } catch (Exception e) {

            }
        }
    }

    @Override
    public void requestStop() throws Exception {
        taskState.requestStop();
        // TODO: set a timeout once we drop support for kafka 0.8
        kafka.close();
    }

    @Override
    public void awaitStop(Long timeout) throws TimeoutException {
        taskState.awaitStop(thread, timeout);
    }

    @Override
    public StoppableTask getStoppableTask() {
        return this;
    }
}


