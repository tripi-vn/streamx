package vn.vntravel.schema;

import org.apache.kafka.common.serialization.*;

import java.util.Map;

public class StreamxSerde<T> implements Serde<T> {
    private final Serde<T> inner;

    @SuppressWarnings("unchecked")
    public StreamxSerde(Class<T> clazz) {
        this.inner = Serdes.serdeFrom(new StringSerializer(), new StreamxDeserializer(clazz));
    }

    @Override
    public Serializer<T> serializer() {
        return this.inner.serializer();
    }

    @Override
    public Deserializer<T> deserializer() {
        return this.inner.deserializer();
    }

    public void configure(Map<String, ?> serdeConfig, boolean isKeys) {
        this.inner.serializer().configure(serdeConfig, isKeys);
        this.inner.deserializer().configure(serdeConfig, isKeys);
    }

    public void close() {
        this.inner.serializer().close();
        this.inner.deserializer().close();
    }
}
