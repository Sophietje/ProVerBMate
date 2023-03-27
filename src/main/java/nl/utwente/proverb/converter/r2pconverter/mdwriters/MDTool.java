package nl.utwente.proverb.converter.r2pconverter.mdwriters;


import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MDTool {

    @Setter
    @Getter
    private File file;

    @Setter
    @Getter
    private String name;

    @Getter
    private final List<String> titles = new ArrayList<>();

    private final Map<String, List<String>> tool = new HashMap<>();

    public List<String> getProperty(String title){
        return tool.get(title);
    }

    public void addProperty(String title, List<String> value){
        tool.put(title, value);
        titles.add(title);
    }

    public void setProperty(String title, List<String> value) {
        tool.put(title, value);
    }
}
