package vn.vntravel.util;

import joptsimple.OptionParser;
import joptsimple.OptionSpecBuilder;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StreamxOptionParser extends OptionParser {
    private String currentSection = null;
    private ArrayList<String> sectionNames = new ArrayList<>();
    private HashMap<String, ArrayList<String>> sections = new HashMap<>();
    private StreamxHelpFormatter helpFormatter = new StreamxHelpFormatter(200, 4, sections, sectionNames);

    public StreamxOptionParser() {
        this.formatHelpWith(helpFormatter);
    }

    public void addToSection(String optionName)  {
        ArrayList<String> list = sections.computeIfAbsent(currentSection, k -> new ArrayList<>());
        list.add(optionName);
    }

    @Override
    public OptionSpecBuilder accepts(String option, String description) {
        addToSection(option);
        return super.accepts(option, description);
    }

    private int separatorIndex = 0;
    public void separator() {
        this.accepts("__separator_" + ++separatorIndex, "");
    }

    public void section(String section) {
        // this puts in a separator not in a section.
        this.accepts("__separator_" + ++separatorIndex);

        this.currentSection = section;
        this.sectionNames.add(section);
    }

    public void printHelpOn(PrintStream err, String section) throws IOException {
        this.helpFormatter.setSection(section);
        super.printHelpOn(err);
    }
}
