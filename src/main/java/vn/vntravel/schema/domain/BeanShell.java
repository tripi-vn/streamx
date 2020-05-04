package vn.vntravel.schema.domain;

/*
 * @created on 4/1/2020
 * @author do.nguyen@tripi.vn
 */

import lombok.Getter;
import lombok.Setter;
import vn.vntravel.schema.domain.bean.Bean;

import java.io.Serializable;

@Getter
@Setter
public final class BeanShell<T> implements Serializable {
    private String database;
    private String table;
    private String type;
    private Long ts;
    private Long xid;
    private Boolean commit;
    private Long xoffset;
    private T data;
    private T old;

    public BeanShell() {}

    @SuppressWarnings("unchecked")
    public BeanShell(T data) {
        Bean bean = (Bean) data;
        Schema _schema = bean._schema();
        this.database = _schema.getDatabase();
        this.table = _schema.getTable();
        this.type = _schema.getType();
        this.ts = _schema.getTs();
        this.xid = _schema.getXid();
        this.commit = _schema.getCommit();
        this.xoffset = _schema.getXoffset();
        this.data = data;
        this.old = (T) bean._old();
    }

    public Schema getSchema() {
        return new Schema(database, table, type, ts, xid, commit, xoffset);
    }
}
