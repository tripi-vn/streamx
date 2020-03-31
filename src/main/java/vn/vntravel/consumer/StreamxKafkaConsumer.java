package vn.vntravel.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.StreamxContext;
import vn.vntravel.schema.StreamxDeserializer;
import vn.vntravel.schema.domain.StreamxFk;
import vn.vntravel.schema.domain.bean.Order;
import vn.vntravel.util.StoppableTask;
import vn.vntravel.util.StoppableTaskState;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

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

    private static StreamxDeserializer<StreamxFk> fkDeserializer = new StreamxDeserializer<>(StreamxFk.class,
            Collections.singletonMap(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));
    private static StreamxDeserializer<Order> dataDeserializer = new StreamxDeserializer<>(Order.class,
            Collections.singletonMap(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false));

    private final Consumer<StreamxFk, Order> kafka;
    private final String topic;
    private final boolean interpolateTopic;
    private Thread thread;
    private StoppableTaskState taskState;

    public StreamxKafkaConsumerWorker(StreamxContext context, String kafkaTopic, Consumer<StreamxFk, Order> consumer) {
        super(context);
        this.kafka = consumer;
        kafkaTopic = "core_aclicktogo_credit_history";
        if ( kafkaTopic == null ) {
            this.topic = "maxwell";
        } else {
            this.topic = kafkaTopic;
        }
        this.interpolateTopic = this.topic.contains("%");
        List<PartitionInfo> partitions = consumer.partitionsFor(this.topic);
        Set<TopicPartition> topicPartitions = partitions.stream()
                .map(p -> new TopicPartition(p.topic(), p.partition())).collect(Collectors.toSet());
        this.kafka.assign(topicPartitions);
        consumer.committed(topicPartitions).forEach((key, value) -> {
            if (value == null) this.kafka.seekToBeginning(Collections.singleton(key));
        });
    }

    public StreamxKafkaConsumerWorker(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        this(context, kafkaTopic, new KafkaConsumer<>(kafkaProperties, fkDeserializer, dataDeserializer));
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
        while ( true ) {
            try {
                ConsumerRecords<StreamxFk, Order> consumerRecords = kafka.poll(Duration.ofMillis(1000));
                //print each record.
                consumerRecords.forEach(record -> {
                    System.out.println("Record Key " + record.key());
                    System.out.println("Record value " + record.value());
                    System.out.println("Record partition " + record.partition());
                    System.out.println("Record offset " + record.offset());
                });
                // commits the offset of record to broker.
                kafka.commitSync();
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


