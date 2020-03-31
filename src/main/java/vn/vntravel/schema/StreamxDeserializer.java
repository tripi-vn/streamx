package vn.vntravel.schema;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class StreamxDeserializer<T extends Serializable> implements Deserializer<T> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> clazz;

    public StreamxDeserializer(Class<T> clazz) {
        this(clazz, Collections.emptyMap());
    }

    public StreamxDeserializer(Class<T> clazz, final Map<DeserializationFeature, Boolean> deserializationFeatures) {
        deserializationFeatures.forEach((f, v) -> objectMapper.configure(f, v));
        this.clazz = clazz;
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null)
            return null;
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
