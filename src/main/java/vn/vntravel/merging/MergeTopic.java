package vn.vntravel.merging;

import java.util.List;
import java.util.Map;

public class MergeTopic {

    private List<String> topics;

    private List<JoinTable> joinTables;
    // <topic, columns>
    private Map<String, List<String>> excludeColumn;
    // <topic, columns>
    private Map<String, List<String>> includeColumn;

    public MergeTopic() {
    }

    public MergeTopic(List<String> topics, List<JoinTable> joinTables, Map<String, List<String>> excludeMap,
                      Map<String, List<String>> includeMap) {
        this.topics = topics;
        this.excludeColumn = excludeMap;
        this.includeColumn = includeMap;
        this.joinTables = joinTables;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Map<String, List<String>> getExcludeColumn() {
        return excludeColumn;
    }

    public void setExcludeColumn(Map<String, List<String>> excludeMap) {
        this.excludeColumn = excludeMap;
    }

    public Map<String, List<String>> getIncludeColumn() {
        return includeColumn;
    }

    public void setIncludeColumn(Map<String, List<String>> includeMap) {
        this.includeColumn = includeMap;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    public void setJoinTables(List<JoinTable> joinTables) {
        this.joinTables = joinTables;
    }
}
