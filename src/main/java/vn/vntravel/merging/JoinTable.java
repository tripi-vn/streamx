package vn.vntravel.merging;

import java.util.Map;

public class JoinTable {
    private String TypeJoin;
    private Map<String, String> mergeMap;

    public JoinTable() {
    }

    public String getTypeJoin() {
        return TypeJoin;
    }

    public void setTypeJoin(String typeJoin) {
        TypeJoin = typeJoin;
    }

    public Map<String, String> getMergeMap() {
        return mergeMap;
    }

    public void setMergeMap(Map<String, String> mergeMap) {
        this.mergeMap = mergeMap;
    }
}
