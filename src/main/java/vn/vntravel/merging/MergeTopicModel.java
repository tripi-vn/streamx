package vn.vntravel.merging;

import java.util.List;
import java.util.Map;

public class MergeTopicModel {

    private List<String> topics;
    private List<Map<String, String>> mergerMaps;
    private Map<String, List<String>> excludeMap;
    private Map<String, List<String>> includeMap;

    public MergeTopicModel(List<String> topics, List<Map<String, String>> mergerMaps,
                           Map<String, List<String>> excludeMap, Map<String, List<String>> includeMap) {
        this.topics = topics;
        this.mergerMaps = mergerMaps;
        this.excludeMap = excludeMap;
        this.includeMap = includeMap;
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

    public Map<String, List<String>> getExcludeMap() {
        return excludeMap;
    }

    public void setExcludeMap(Map<String, List<String>> excludeMap) {
        this.excludeMap = excludeMap;
    }

    public Map<String, List<String>> getIncludeMap() {
        return includeMap;
    }

    public void setIncludeMap(Map<String, List<String>> includeMap) {
        this.includeMap = includeMap;
    }
}
