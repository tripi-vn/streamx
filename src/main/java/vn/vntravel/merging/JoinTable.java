package vn.vntravel.merging;

import java.util.Map;

public class JoinTable {
    private TypeJoin typeJoin;
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
