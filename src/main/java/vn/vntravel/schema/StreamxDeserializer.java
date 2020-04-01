package vn.vntravel.schema;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import vn.vntravel.schema.domain.Schema;
import vn.vntravel.schema.domain.bean.Bean;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class StreamxDeserializer<T extends Serializable> implements Deserializer<T> {
    private static ObjectMapper dataFormMapper = new ObjectMapper();
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
                DataForm dataForm = dataFormMapper.readValue(bytes, DataForm.class);
                data = objectMapper.treeToValue(dataForm.data, clazz);
                ((Bean) data)._set(dataForm.getSchema());
                if (dataForm.old != null) {
                    T old = objectMapper.treeToValue(dataForm.old, clazz);
                    ((Bean) data)._setOld((Bean) old);
                }
            } else {
                data = objectMapper.readValue(bytes, clazz);
            }
            return data;
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }

    private static class DataForm {
        private String database;
        private String table;
        private String type;
        private Long ts;
        private Long xid;
        private Boolean commit;
        private Long xoffset;
        private JsonNode data;
        private JsonNode old;

        public Schema getSchema() {
            return new Schema(database, table, type, ts, xid, commit, xoffset);
        }

        public JsonNode getOld() {
            return old;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public void setTable(String table) {
            this.table = table;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setTs(Long ts) {
            this.ts = ts;
        }

        public void setXid(Long xid) {
            this.xid = xid;
        }

        public void setCommit(Boolean commit) {
            this.commit = commit;
        }

        public void setXoffset(Long xoffset) {
            this.xoffset = xoffset;
        }

        public void setData(JsonNode data) {
            this.data = data;
        }

        public void setOld(JsonNode old) {
            this.old = old;
        }
    }
}
