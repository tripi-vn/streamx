package vn.vntravel.schema.domain.bean;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.avro.Schema;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
public class Order implements Serializable {
    private String database;
    private String table;
    private Object id;
    private static Pattern pattern = Pattern.compile("pk.[a-zA-Z_]+$");
    @JsonAnySetter
    public void setId(String field, Object id) {
        if (pattern.matcher(field).matches())
            this.id = id;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
