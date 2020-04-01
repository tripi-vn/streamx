package vn.vntravel.schema;
/*
 * @created on 4/1/2020
 * @author do.nguyen@tripi.vn
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import vn.vntravel.schema.domain.BeanShell;
import vn.vntravel.schema.domain.bean.Bean;

import java.io.Serializable;

public class StreamxSerializer<T extends Serializable> implements Serializer<T> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> clazz;
    public StreamxSerializer(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public byte[] serialize(String topic, T data) {
        if (data == null)
            return null;
        try {
            if (Bean.class.isAssignableFrom(clazz)) {
                BeanShell<T> bean = new BeanShell<>(data);
                return objectMapper.writeValueAsBytes(bean);
            } else return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing JSON message", e);
        }
    }
}
