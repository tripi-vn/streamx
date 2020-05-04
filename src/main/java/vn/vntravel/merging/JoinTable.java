package vn.vntravel.merging;

import org.apache.commons.collections4.KeyValue;

import java.util.Map;

public class JoinTable {
    private TypeJoin typeJoin;
    private KeyValue<String, String> pk;
    private KeyValue<String, String> fk;
    private Map<String, String> mergeMap;

    public JoinTable() {
    }

    public String getTypeJoin() {
        return typeJoin.getType();
    }

    public void setTypeJoin(String typeJoin) {
        this.typeJoin = TypeJoin.fromType(typeJoin);
    }

    public Map<String, String> getMergeMap() {
        return mergeMap;
    }

    public void setMergeMap(Map<String, String> mergeMap) {
        this.mergeMap = mergeMap;
    }
}
