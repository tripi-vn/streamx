package vn.vntravel.schema.domain;

import java.io.Serializable;

public final class Schema implements Serializable {
    private String database;
    private String table;
    private String type;
    private Long ts;
    private Long xid;
    private Boolean commit;
    private Long xoffset;

    public Schema() {

    }

    public Schema(String database, String table, String type, Long ts, Long xid, Boolean commit, Long xoffset) {
        this.database = database;
        this.table = table;
        this.type = type;
        this.ts = ts;
        this.xid = xid;
        this.commit = commit;
        this.xoffset = xoffset;
    }

    public String getDatabase() {
        return database;
    }

    public String getTable() {
        return table;
    }

    public String getType() {
        return type;
    }

    public Long getTs() {
        return ts;
    }

    public Long getXid() {
        return xid;
    }

    public Boolean getCommit() {
        return commit;
    }

    public Long getXoffset() {
        return xoffset;
    }
}
