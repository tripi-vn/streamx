package vn.vntravel.consumer;

import com.fasterxml.jackson.databind.DeserializationFeature;
import javafx.util.Pair;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vntravel.StreamxContext;
import vn.vntravel.schema.StreamxDeserializer;
import vn.vntravel.schema.StreamxSerde;
import vn.vntravel.schema.domain.StreamxFk;
import vn.vntravel.schema.domain.bean.Order;
import vn.vntravel.schema.domain.bean.OrderItem;
import vn.vntravel.schema.domain.window.OrderWindow;
import vn.vntravel.stream.StreamxKafkaProcessor;
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
        kafkaProperties.put(StreamsConfig.APPLICATION_ID_CONFIG, "bo-window-application");
        kafkaProperties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaProperties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

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


    static final String ORDER_ITEM_STORE = "order_item-store";
    static final String ORDER_ITEM_INFO_TOPIC = "bo_order_item_info";

    public StreamxKafkaConsumerWorker(StreamxContext context, String kafkaTopic, Consumer<StreamxFk, Order> consumer, Properties kafkaProperties) {
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

        Map<DeserializationFeature, Boolean> futures = new HashMap<>();
        futures.put(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        futures.put(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        StreamsBuilder builder = new StreamsBuilder();
        final StreamxSerde<Order> orderSerde = new StreamxSerde<>(Order.class, futures);
        final StreamxSerde<OrderItem> orderItemSerde = new StreamxSerde<>(OrderItem.class, futures);
        final StreamxSerde<OrderWindow> orderWindowSerde = new StreamxSerde<>(OrderWindow.class);

        final StreamxSerde<StreamxFk> fkOrderSerde = new StreamxSerde<>(StreamxFk.class);
        final StreamxSerde<StreamxFk> fkOrderItemSerde = new StreamxSerde<>(StreamxFk.class);
        final StreamxSerde<StreamxFk> fkOrderWindowSerde = new StreamxSerde<>(StreamxFk.class);

        final KStream<StreamxFk, OrderItem> orderItems = builder.stream("core_aclicktogo_orders_items",
                Consumed.with(fkOrderItemSerde, orderItemSerde));
        final KTable<StreamxFk, Order>
                orders = builder.table("core_aclicktogo_orders", Materialized.<StreamxFk, Order, KeyValueStore<Bytes, byte[]>>as(ORDER_ITEM_STORE)
                .withKeySerde(fkOrderSerde)
                .withValueSerde(orderSerde));

        final KStream<StreamxFk, OrderWindow> orderWindows = orderItems
                .join(orders, OrderWindow::new);

//        final KStream<StreamxFk, OrderWindow> orderWindowStream = orderItemStream.join(orders, new
//        final KStream<StreamxFk, OrderWindow> orderWindowStream = orderItemStream.join(orders,
//                (orderItemKey, orderItem) -> new StreamxFk(orderItem.getOrderId()),
//                (orderItem, order) -> new OrderWindow(order, orderItem));

        orderWindows.to(ORDER_ITEM_INFO_TOPIC, Produced.with(fkOrderWindowSerde, orderWindowSerde));
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), kafkaProperties);

        kafkaStreams.cleanUp();
        // start processing
        kafkaStreams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    public StreamxKafkaConsumerWorker(StreamxContext context, Properties kafkaProperties, String kafkaTopic) {
        this(context, kafkaTopic, new KafkaConsumer<>(kafkaProperties, fkDeserializer, dataDeserializer), kafkaProperties);
    }

    @Override
    public void run() {
        this.thread = Thread.currentThread();
//        while ( true ) {
//            try {
//                ConsumerRecords<StreamxFk, Order> consumerRecords = kafka.poll(Duration.ofMillis(1000));
//                //print each record.
//                consumerRecords.forEach(record -> {
//                    System.out.println("Record Key " + record.key());
//                    System.out.println("Record value " + record.value());
//                    System.out.println("Record partition " + record.partition());
//                    System.out.println("Record offset " + record.offset());
//                });
//                // commits the offset of record to broker.
//                kafka.commitSync();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
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


