package vn.vntravel.stream;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import vn.vntravel.StreamxContext;
import vn.vntravel.consumer.AbstractAsyncConsumer;
import vn.vntravel.schema.StreamxSerde;
import vn.vntravel.schema.domain.StreamxFk;
import vn.vntravel.schema.domain.bean.Order;
import vn.vntravel.schema.domain.bean.OrderItem;
import vn.vntravel.util.StoppableTask;

import java.io.Serializable;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

/**
 * Stream data
 */
public class StreamxKafkaProcessor extends AbstractAsyncConsumer implements Runnable, StoppableTask {
    static final String ORDER_ITEM_STORE = "order_item-store";
    static final String ORDER_ITEM_INFO_TOPIC = "bo_order_item_info";
    private Properties props;

    public StreamxKafkaProcessor(StreamxContext context, Properties kafkaProperties) {
        super(context);
        this.props = kafkaProperties;
        this.props.put(StreamsConfig.APPLICATION_ID_CONFIG, "bo-window-application");
        this.props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        this.props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    }

    @Override
    public void run() {
        StreamsBuilder builder = new StreamsBuilder();
        final StreamxSerde<Order> orderSerde = new StreamxSerde<>(Order.class);
        final StreamxSerde<OrderItem> orderItemSerde = new StreamxSerde<>(OrderItem.class);
        final StreamxSerde<OrderWindow> orderWindowSerde = new StreamxSerde<>(OrderWindow.class);

        final StreamxSerde<StreamxFk> fkOrderSerde = new StreamxSerde<>(StreamxFk.class);
        final StreamxSerde<StreamxFk> fkOrderItemSerde = new StreamxSerde<>(StreamxFk.class);
        final StreamxSerde<StreamxFk> fkOrderWindowSerde = new StreamxSerde<>(StreamxFk.class);

        final KStream<StreamxFk, OrderItem> orderItemStream = builder.stream("core_aclicktogo_orders_items", Consumed.with(fkOrderItemSerde, orderItemSerde));
        final GlobalKTable<StreamxFk, Order>
                orders = builder.globalTable("core_aclicktogo_orders", Materialized.<StreamxFk, Order, KeyValueStore<Bytes, byte[]>>as(ORDER_ITEM_STORE)
                .withKeySerde(fkOrderSerde)
                .withValueSerde(orderSerde));

        final KStream<StreamxFk, OrderWindow> orderWindowStream = orderItemStream.join(orders,
                (orderItemKey, orderItem) -> new StreamxFk(orderItem.getOrderId()),
                (orderItem, order) -> new OrderWindow(order, orderItem));

        orderWindowStream.to(ORDER_ITEM_INFO_TOPIC, Produced.with(fkOrderWindowSerde, orderWindowSerde));
        KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props);

        kafkaStreams.cleanUp();
        // start processing
        kafkaStreams.start();
        // Add shutdown hook to respond to SIGTERM and gracefully close Kafka Streams
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private static class OrderWindow implements Serializable {
        private Order order;
        private OrderItem orderItem;

        public OrderWindow(Order order, OrderItem orderItem) {
            this.order = order;
            this.orderItem = orderItem;
        }
    }

    @Override
    public void requestStop() throws Exception {

    }

    @Override
    public void awaitStop(Long timeout) throws TimeoutException {

    }
}
