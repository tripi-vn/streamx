package vn.vntravel.schema.domain;

import com.fasterxml.jackson.annotation.JsonAnySetter;

import java.io.Serializable;
import java.util.regex.Pattern;

public class StreamxFk implements Serializable {
    private static final long serialVersionUID = 10987L;

    private static Pattern fkPattern = Pattern.compile("pk.[a-zA-Z_]+$");
    private String database;
    private String table;
    private Object fkId;

    public StreamxFk() {

    }

    public StreamxFk(Object fkId) {
        this.fkId = fkId;
    }

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

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public Object getFkId() {
        return fkId;
    }

    /**
     * Compare foreign key value
     * @param v
     * @return
     */
    private boolean eqFk(Integer v) {
        return (int) fkId == v;
    }

    private boolean eqFk(Double v) {
        return (double) fkId == v;
    }

    private boolean eqFk(Float v) {
        return (float) fkId == v;
    }

    private boolean eqFk(Long v) {
        return (long) fkId == v;
    }

    private boolean eqFk(String v) {
        return fkId.equals(v);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;

        if (!(o instanceof StreamxFk))
            return false;

        StreamxFk fk = (StreamxFk) o;

        if (fk.fkId instanceof Long)
            return eqFk((Long) fk.fkId);

        if (fk.fkId instanceof Integer)
            return eqFk((Integer) fk.fkId);

        if (fk.fkId instanceof Double)
            return eqFk((Double) fk.fkId);

        if (fk.fkId instanceof Float)
            return eqFk((Float) fk.fkId);

        if (fk.fkId instanceof String)
            return eqFk((String) fk.fkId);

        return false;
    }
}
