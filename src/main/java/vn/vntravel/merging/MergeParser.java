package vn.vntravel.merging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class MergeParser {

    private Properties properties;

    public List<MergeTopicModel> parse(String filename) {
        List<MergeTopicModel> mergeTopicModels = new ArrayList<>();
        this.properties = readPropertiesFile(filename);
        if (properties.size() == 0) {
            System.err.println("File empty " + filename);
            return null;
        }
        String value = properties.getProperty("merge");
        if (value == null) {
            System.err.println("Couldn't find with key 'merge'");
            return null;
        }
        String[] mergeTopicStrings = value.split(Pattern.quote("],"));
        for (String mergeString : mergeTopicStrings) {
            mergeTopicModels.add(createObject(mergeString));
        }
        return mergeTopicModels;
    }

    private MergeTopicModel createObject(String mergeString) {
        String[] merges = mergeString.split(Pattern.quote("]"));
        List<String> topics = new ArrayList<>();
        List<Map<String, String>> mergeMaps = new ArrayList<>();
        Map<String, List<String>> excludeMap = new HashMap<>();
        Map<String, List<String>> includeMap = new HashMap<>();

        findTopicAndColumnJoin(topics, mergeMaps, merges[0].split(Pattern.quote("}")));
        findExcludeAndInclude(excludeMap, includeMap, merges[1], topics);

        return new MergeTopicModel(topics, mergeMaps, excludeMap, includeMap);
    }

    private void findExcludeAndInclude(Map<String, List<String>> excludeMap, Map<String, List<String>> includeMap, String findString, List<String> topics) {
        findString = removeSpecialCharacter(findString);
        if (findString.length() <= 1) return;
        for (String topic : topics) {
            excludeMap.put(topic, new ArrayList<>());
            includeMap.put(topic, new ArrayList<>());
        }
        StringTokenizer stringTokenizer = new StringTokenizer(findString);
        boolean isInclude = false;
        while (stringTokenizer.hasMoreTokens()) {
            String value = stringTokenizer.nextToken();
            if (value.equals("include")) {
                isInclude = true;
                continue;
            }
            if (value.equals("exclude")) {
                isInclude = false;
                continue;
            }
            String[] columnString = value.split(Pattern.quote("."));
            if (isInclude) {
                excludeMap.get(columnString[0]).add(columnString[1]);
            } else includeMap.get(columnString[0]).add(columnString[1]);
        }
    }

    private void findTopicAndColumnJoin(List<String> topics, List<Map<String, String>> mergeMaps, String[] findStrings) {
        for (String merge : findStrings) {
            merge = removeSpecialCharacter(merge);
            StringTokenizer tokenizer = new StringTokenizer(merge);
            boolean isColumnJoin = false;
            Map<String, String> mergeMap = new HashMap<>();
            while (tokenizer.hasMoreTokens()) {
                String value = tokenizer.nextToken();
                if (value.equals("on")) {
                    isColumnJoin = true;
                    continue;
                }
                if (!isColumnJoin) {
                    if (!topics.contains(value))
                        topics.add(value);
                } else {
                    String[] columnString = value.split(Pattern.quote("."));
                    mergeMap.put(columnString[0], columnString[1]);
                }
            }
            mergeMaps.add(mergeMap);
        }
    }

    private String removeSpecialCharacter(String value) {
        value = value.replaceAll("[^a-zA-Z0-9\\s\\.\\_\\*+]", "");
        return value;
    }

    private Properties readPropertiesFile(String filename) {
        Properties p = null;
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("Couldn't find config file: " + filename);
            System.exit(1);
        }
        try {
            FileReader reader = new FileReader(file);
            p = new Properties();
            p.load(reader);
        } catch (IOException e) {
            System.err.println("Couldn't read config file: " + e);
            System.exit(1);
        }
        return p;
    }


}
