package vn.vntravel.merging;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

public class MergeParserTest {
    private List<MergeTopic> mergeTopics;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    private List<MergeTopic> runParseTest(String filename){
        return new MergeParser().parse(filename);
    }

    @Test
    public void TestParserModel() throws Exception{
        mergeTopics = runParseTest("src/test/resources/merge-test.properties");
        assertEquals("Check number object: ",2, mergeTopics.size());
        assertEquals("Check number topics object1: ", 3, mergeTopics.get(0).getTopics().size());
        assertEquals("Check number topics object2: ",2, mergeTopics.get(1).getTopics().size());
    }

    @Test
    public void TestFindColumnJoin() throws Exception{
        mergeTopics = runParseTest("src/test/resources/merge-test.properties");

//        MergeTopic model1 = mergeTopics.get(0);
//        assertEquals("Check size column-join object1: ",2, model1.get().size());
//        List<Map<String, String>> mergeMaps1 = model1.getMergerMaps();
//        assertEquals("Check column-map size topic1 in object1: ",2, mergeMaps1.get(0).size());
//        assertEquals("Check column-map size topic2 in object1: ",2, mergeMaps1.get(1).size());
//
//        MergeTopic model2 = mergeTopics.get(1);
//        assertEquals("Check size column-join object2: ",1, model2.getMergerMaps().size());
//        List<Map<String, String>> mergeMaps2 = model2.getMergerMaps();
//        assertEquals("Check column-map size topic in object2",2, mergeMaps2.get(0).size());
    }

    @Test
    public void testFindExcludeAndInclude() throws Exception{
        mergeTopics = runParseTest("src/test/resources/merge-test.properties");

        MergeTopic model1 = mergeTopics.get(0);
        assertEquals("Check excludeColumn size object1: ",2, model1.getExcludeColumn().size());
        assertEquals("Check includeColumn size object1: ",1, model1.getIncludeColumn().size());
        Map<String, List<String>> excludeModel1 = model1.getExcludeColumn();
        Map<String, List<String>> includeModel1 = model1.getIncludeColumn();
        assertEquals("Check values size excludeColumn in object1: ",2, excludeModel1.get("topic2").size());
        assertEquals("Check values size includeColumn in object1: ",1, includeModel1.get("topic2").size());

        MergeTopic model2 = mergeTopics.get(1);
        assertEquals("Check excludeColumn size object2: ",0, model2.getIncludeColumn().size());
        assertEquals("Check includeColumn size object2: ",0, model2.getExcludeColumn().size());
    }

    @Test
    public void testWithErrorFormFile() throws Exception{
        mergeTopics = runParseTest("src/test/resources/wrong-format-test.properties");
        assertEquals("Check right-format object size: ", 1, mergeTopics.size());
    }
}
