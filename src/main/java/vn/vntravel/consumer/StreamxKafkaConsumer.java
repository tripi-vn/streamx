package vn.vntravel.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.StreamxContext;
import vn.vntravel.util.StoppableTask;
import vn.vntravel.util.StoppableTaskState;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

class StreamxKafkaCallback {

}

public class StreamxKafkaConsumer extends AbstractConsumer {
    private final StreamxKafkaConsumerWorker worker;
    public StreamxKafkaConsumer(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        super(context);
        this.worker = new StreamxKafkaConsumerWorker(context, kafkaProperties, kafkaTopic);
        Thread thread = new Thread(this.worker, "streamx-kafka-worker");
        thread.setDaemon(true);
        thread.start();
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

        TopicPartition topicPartition = new TopicPartition("core_backoffice_test", 0);
        List<TopicPartition> topics = Arrays.asList(topicPartition);
        this.kafka.assign(topics);
        this.kafka.seekToEnd(topics);
        long current = consumer.position(topicPartition);
        this.kafka.seek(topicPartition, current);

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
                ConsumerRecords<String, String> consumerRecords = kafka.poll(Duration.ofMillis(1000));
                //print each record.
                consumerRecords.forEach(record -> {
                    System.out.println("Record Key " + record.key());
                    System.out.println("Record value " + record.value());
                    System.out.println("Record partition " + record.partition());
                    System.out.println("Record offset " + record.offset());
                });
                // commits the offset of record to broker.
                kafka.commitAsync();
            } catch (Exception e) {
                e.printStackTrace();
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


