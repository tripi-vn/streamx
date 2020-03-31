package vn.vntravel.schema.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Getter;

import java.io.Serializable;
import java.util.regex.Pattern;

@Getter
public class StreamxFk implements Serializable {
    private static final long serialVersionUID = 10987L;

    private static Pattern fkPattern = Pattern.compile("pk.[a-zA-Z_]+$");
    private String database;
    private String table;
    private Object fkId;

    @JsonAnySetter
    public void setId(String field, Object fkId) {
        if (fkPattern.matcher(field).matches())
            this.fkId = fkId;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Compare foreign key value
     * @param v
     * @return
     */
    public boolean eqFk(Integer v) {
        return fkId == v;
    }

    public boolean eqFk(Double v) {
        return fkId == v;
    }

    public boolean eqFk(Float v) {
        return fkId == v;
    }

    public boolean eqFk(Long v) {
        return fkId == v;
    }

    public boolean eqFk(String v) {
        return fkId == v;
    }
}
