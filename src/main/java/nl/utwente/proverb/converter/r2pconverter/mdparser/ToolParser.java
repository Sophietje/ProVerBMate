package nl.utwente.proverb.converter.r2pconverter.mdparser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolParser {

    private final File tool;

    public ToolParser(File tool){
        this.tool = tool;
    }

    public MDTool read(File input){
        MDTool tool = new MDTool();
        tool.setName(input.getName());
        try (var reader = new InputStreamReader(new FileInputStream(input));
             var br = new BufferedReader(reader)){
            String line;
            line = br.readLine();
            while (line != null) {
                switch (line){
                    case MDToolTemplate.FULL_NAME:
                        var name = readSection(br);
                        tool.addProperty(MDToolTemplate.FULL_NAME, name);
                        break;
                    case MDToolTemplate.DOMAIN:
                        var domains = readSection(br);
                        tool.addProperty(MDToolTemplate.DOMAIN, domains);
                        break;
                    case MDToolTemplate.TYPE:
                        var types = readSection(br);
                        tool.addProperty(MDToolTemplate.TYPE, types);
                        break;
                    case MDToolTemplate.INPUT_THING:
                        List<String> thing = readSection(br);
                        tool.addProperty(MDToolTemplate.INPUT_THING, thing);
                        break;
                    case MDToolTemplate.INPUT_FORMAT:
                        List<String> inputs = readSection(br);
                        tool.addProperty(MDToolTemplate.INPUT_FORMAT, inputs);
                        break;
                    case MDToolTemplate.OUTPUT:
                        List<String> outputs = readSection(br);
                        tool.addProperty(MDToolTemplate.OUTPUT, outputs);
                        break;
                    case MDToolTemplate.INTERNAL:
                        List<String> internals = readSection(br);
                        tool.addProperty(MDToolTemplate.INTERNAL, internals);
                        break;
                    case MDToolTemplate.COMMENT:
                        List<String> comments = readSection(br);
                        tool.addProperty(MDToolTemplate.COMMENT, comments);
                        break;
                    case MDToolTemplate.URIS:
                        List<String> uris = readSection(br);
                        tool.addProperty(MDToolTemplate.URIS, uris);
                        break;
                    case MDToolTemplate.LAST_COMMIT_DATE:
                        List<String> cmtDate = readSection(br);
                        tool.addProperty(MDToolTemplate.LAST_COMMIT_DATE, cmtDate);
                        break;
                    case MDToolTemplate.LAST_PUB_DATE:
                        List<String> pubDate = readSection(br);
                        tool.addProperty(MDToolTemplate.LAST_PUB_DATE, pubDate);
                        break;
                    case MDToolTemplate.PAPERS:
                        List<String> papers = readSection(br);
                        tool.addProperty(MDToolTemplate.PAPERS, papers);
                        break;
                    case MDToolTemplate.RELA_TOOLS:
                        List<String> relaTools = readSection(br);
                        tool.addProperty(MDToolTemplate.RELA_TOOLS, relaTools);
                        break;
                    case MDToolTemplate.META:
                        List<String> metas = readSection(br);
                        tool.addProperty(MDToolTemplate.META, metas);
                        break;
                    case "":
                        break;
                    default:
                        List<String> defaults = readSection(br);
                        tool.addProperty(line, defaults);
                        System.err.println("Undefined header in "+input.getName()+": "+line);
                        break;
                }
                if (memory != null) line = memory; else line = br.readLine();
            }
        } catch (IOException e) {
            System.err.println("Reading file error:" +e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }
        return tool;
    }

    String memory; //Remember what you've read if it was not supposed to be a part of this section
    public List<String> readSection(BufferedReader br) throws IOException{
        memory = null;
        String line = br.readLine();
        List<String> content = new ArrayList<>();
        while (line != null && !line.contains("####")){
            content.add(line);
            line = br.readLine();
        }
        memory = line;
        return content;
    }

    public void write(MDTool mdTool){
        try (var pw = new PrintWriter(new FileWriter(tool))){
            StringBuilder builder = new StringBuilder();
            for (var title : mdTool.getTitles()){
                builder.append(combineSeg(title, mdTool.getProperty(title)));
            }
            pw.println(builder);
        }catch (IOException e){
            System.err.println(e.getStackTrace().toString());
        }
    }

    public String combineSeg(String title, List<String> contents){
        StringBuilder builder = new StringBuilder();
        builder.append(title).append("\n");
        contents.forEach(c -> builder.append(c).append("\n"));
        return builder.toString();
    }
}
