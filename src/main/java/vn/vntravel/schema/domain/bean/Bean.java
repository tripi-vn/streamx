package vn.vntravel.schema.domain.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import vn.vntravel.schema.domain.Schema;

import java.io.Serializable;

public abstract class Bean implements Serializable {
    @JsonIgnore
    private Schema _schema;
    private Bean _old;

    public void _set(Schema _schema) {
        this._schema = _schema;
    }

    public void _setOld(Bean _old) {
        this._old = _old;
    }
}
