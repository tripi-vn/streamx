package vn.vntravel.merging;

import java.util.List;
import java.util.Map;

public class MergeTopicModel {

    private List<String> topics;
    private List<Map<String, String>> mergerMaps;
    private Map<String, List<String>> excludeColumn;
    private Map<String, List<String>> includeColumn;

    public MergeTopicModel(List<String> topics, List<Map<String, String>> mergerMaps,
                           Map<String, List<String>> excludeMap, Map<String, List<String>> includeMap) {
        this.topics = topics;
        this.mergerMaps = mergerMaps;
        this.excludeColumn = excludeMap;
        this.includeColumn = includeMap;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public List<Map<String, String>> getMergerMaps() {
        return mergerMaps;
    }

    public void setMergerMaps(List<Map<String, String>> mergerMaps) {
        this.mergerMaps = mergerMaps;
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
}
