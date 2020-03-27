package vn.vntravel.merging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class MergeParser {

    private Properties properties;
    private String COLUMN_PATTERN = "^\\w+\\.\\w+|\\w+.\\*$";
    private String REMOVE_CHARACTER_SPECIAL_PATTERN = "[^a-zA-Z0-9\\s\\.\\_\\*+]";

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
            try {
                mergeTopicModels.add(createObject(mergeString));
            } catch (FormFormatException e) {
                System.err.println("Exception: " + e.getMessage());
            } catch (MergerException e) {
                System.err.println("Exception: " + e.getMessage());
            }
        }
        return mergeTopicModels;
    }

    private MergeTopicModel createObject(String mergeString) throws FormFormatException, MergerException {
        String[] merges = mergeString.split(Pattern.quote("]"));
        if (merges.length > 2) throw new FormFormatException("Wrong form-format: " + mergeString);
        List<String> topics = new ArrayList<>();
        List<Map<String, String>> mergeMaps = new ArrayList<>();
        Map<String, List<String>> excludeColumn = new HashMap<>();
        Map<String, List<String>> includeColumn = new HashMap<>();

        findTopicAndColumnJoin(topics, mergeMaps, merges[0].split(Pattern.quote("}")));
        findExcludeAndInclude(excludeColumn, includeColumn, merges[1], topics);

        return new MergeTopicModel(topics, mergeMaps, excludeColumn, includeColumn);
    }

    private void findExcludeAndInclude(Map<String, List<String>> excludeColumn, Map<String, List<String>> includeColumn,
                                       String findString, List<String> topics) throws FormFormatException, MergerException {
        findString = removeSpecialCharacter(findString);
        if (findString.length() <= 1) return;
        for (String topic : topics) {
            excludeColumn.put(topic, new ArrayList<>());
            includeColumn.put(topic, new ArrayList<>());
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
            if (!Pattern.matches(COLUMN_PATTERN, value))
                throw new FormFormatException("Wrong form-format: " + findString);
            String[] columnString = value.split(Pattern.quote("."));
            if (columnString[1].equals("*")) continue;
            if (!isInclude) {
                if (!excludeColumn.containsKey(columnString[0]))
                    throw new MergerException("Topic '" + columnString[0] + "' not found in list topics: " + findString);
                excludeColumn.get(columnString[0]).add(columnString[1]);
            } else {
                if (!includeColumn.containsKey(columnString[0]))
                    throw new MergerException("Topic '" + columnString[0] + "' not found in list topics: " + findString);
                includeColumn.get(columnString[0]).add(columnString[1]);
            }
        }
        excludeColumn.values().removeIf(value -> value.size() == 0);
        includeColumn.values().removeIf(value -> value.size() == 0);
    }

    private void findTopicAndColumnJoin(List<String> topics, List<Map<String, String>> mergeMaps, String[] findStrings) throws FormFormatException {
        for (String merge : findStrings) {
            merge = removeSpecialCharacter(merge);
            StringTokenizer tokenizer = new StringTokenizer(merge);
            if(tokenizer.countTokens() <= 2) throw new FormFormatException("Wrong form-format: "+ merge);
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
                    if (!Pattern.matches(COLUMN_PATTERN, value))
                        throw new FormFormatException("Wrong form-format: " + merge);
                    String[] columnString = value.split(Pattern.quote("."));
                    mergeMap.put(columnString[0], columnString[1]);
                }
            }
            mergeMaps.add(mergeMap);
        }
    }

    private String removeSpecialCharacter(String value) {
        value = value.replaceAll(REMOVE_CHARACTER_SPECIAL_PATTERN, "");
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
