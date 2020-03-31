package vn.vntravel.merging;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class MergeParser {

    private Properties properties;
    private String COLUMN_PATTERN = "^(\\w+\\.\\w+)|\\*$";
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
            } catch (FormFormatException | MergerException e) {
                System.err.println("Exception: " + e.getMessage());
            }
        }
        return mergeTopicModels;
    }

    private MergeTopicModel createObject(String mergeString) throws FormFormatException, MergerException {
        MergeTopicModel mergeModel = new MergeTopicModel();
        String[] merges = mergeString.split(Pattern.quote("]"));
        if (merges.length > 2) throw new FormFormatException("Wrong form-format: " + mergeString);
        List<String> topics = new ArrayList<>();

        findTopicAndColumnJoin(mergeModel, topics, merges[0].split(Pattern.quote("}")));
        findExcludeAndInclude(mergeModel, merges[1], topics);

        return mergeModel;
    }

    private void findExcludeAndInclude(MergeTopicModel mergeTopicModel, String findString, List<String> topics) throws FormFormatException, MergerException {
        Map<String, List<String>> excludeColumn = new HashMap<>();
        Map<String, List<String>> includeColumn = new HashMap<>();
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
            if (value.length() == 1) continue;
            String[] columnString = value.split(Pattern.quote("."));
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
        mergeTopicModel.setExcludeColumn(excludeColumn);
        mergeTopicModel.setIncludeColumn(includeColumn);
    }

    private void findTopicAndColumnJoin(MergeTopicModel mergeTopicModel, List<String> topics, String[] findStrings) throws FormFormatException {
        List<JoinTable> joinTables = new ArrayList<>();
        for (String merge : findStrings) {
            merge = removeSpecialCharacter(merge);
            StringTokenizer tokenizer = new StringTokenizer(merge);
            if (tokenizer.countTokens() <= 2) throw new FormFormatException("Wrong form-format: " + merge);
            boolean isColumnJoin = false;
            JoinTable joinTable = new JoinTable();
            Map<String, String> mergeMap = new HashMap<>();
            while (tokenizer.hasMoreTokens()) {
                String value = tokenizer.nextToken();
                if (value.equals(TypeJoin.JOIN.getType()) || value.equals(TypeJoin.LJOIN.getType())
                        || value.equals(TypeJoin.RJOIN.getType())) {
                    isColumnJoin = true;
                    joinTable.setTypeJoin(value);
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
            joinTable.setMergeMap(mergeMap);
            joinTables.add(joinTable);
        }
        mergeTopicModel.setTopics(topics);
        mergeTopicModel.setJoinTables(joinTables);
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
