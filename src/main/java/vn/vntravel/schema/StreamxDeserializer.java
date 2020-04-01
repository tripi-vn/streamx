package vn.vntravel.schema;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import vn.vntravel.schema.domain.BeanShell;
import vn.vntravel.schema.domain.bean.Bean;

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
        T data;
        try {
            if (Bean.class.isAssignableFrom(clazz)) {
                JavaType type = objectMapper.getTypeFactory().constructParametricType(BeanShell.class, clazz);
                BeanShell<T> beanShell = objectMapper.readValue(bytes, type);
                Bean bean = (Bean) beanShell.getData();
                bean._set(beanShell.getSchema());
                if (beanShell.getOld() != null)
                    bean._setOld((Bean) beanShell.getOld());
                return beanShell.getData();
            } else {
                data = objectMapper.readValue(bytes, clazz);
            }
            return data;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
