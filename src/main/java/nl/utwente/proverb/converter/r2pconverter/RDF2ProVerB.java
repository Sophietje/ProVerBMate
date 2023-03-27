package nl.utwente.proverb.converter.r2pconverter;

import nl.utwente.proverb.converter.r2pconverter.converter.Tool2Tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RDF2ProVerB {

    private static String modelPath = "enriched_ProVerB_1.6.0.owl";

    private static String toolsPath = "Verification-Tool-Overview/Tools";

    private static String demoTool = "/Users/sophie/Documents/Verification-Tool-Overview/Tools/EntropyEstimation.md";

    public static void main(String[] args) {
        if (args.length == 2){
            modelPath = args[0];
            toolsPath = args[1] + "/Tools";
        }

        var model = loadModelFile();

        var tools = loadTools();
        //var tools = loadDemo();
        Tool2Tool converter;
        for (File tool : tools){
            converter = new Tool2Tool.Builder(model, tool)
                    .loadRelatedPapers(true)
                    .loadRepositories(true)
                    .loadRepoLastCommitTime(true)
                    .build();
            converter.convert();
//            var parser = new ToolParser(tool);
//            var mdTool = parser.read(tool);
//            BaseAnalyzer analyzer = new PaperAnalyzer(model, mdTool);
//            analyzer.autoEnrichment();
//            parser.write(mdTool);
        }
    }

    private static File loadModelFile(){
        return new File(modelPath);
    }

    private static List<File> loadDemo(){
        var list = new ArrayList<File>(1);
        list.add(new File(demoTool));
        return list;
    }

    private static List<File> loadTools(){
        var path = new File(toolsPath);
        if (!path.isDirectory()){
            throw new IllegalArgumentException("Invalid tools path");
        }
        var rootFiles = path.listFiles();
        if (rootFiles == null || rootFiles.length == 0){
            throw new IllegalArgumentException("Invalid tools path");
        }
        return getFiles(new ArrayList<>(List.of(rootFiles)));
    }

    private static List<File> getFiles(List<File> rootFiles){

        var newFiles = new ArrayList<File>();
        for (File file : rootFiles){
            if (file.isDirectory()){
                var fs = file.listFiles();
                if (fs != null){
                    newFiles.addAll(getFiles(new ArrayList<>(List.of(fs))));
                }
            }
            if (file.isFile() && !file.getName().contains("README.md") && !file.getName().contains(".DS_Store")){
                newFiles.add(file);
            }
        }
        return newFiles;
    }
}
