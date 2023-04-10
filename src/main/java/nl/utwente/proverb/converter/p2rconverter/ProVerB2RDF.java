package nl.utwente.proverb.converter.p2rconverter;


import nl.utwente.proverb.ProVerBMateApplication;
import nl.utwente.proverb.converter.p2rconverter.converter.Tool2Tool;
import nl.utwente.proverb.converter.p2rconverter.service.OntologyService;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProVerB2RDF {
    private static String modelPath = "ProVerB_1.6.0.owl";

    private static String toolsPath = "[ENTER DEFAULT PATH]";

    private static final String demoTool = "ACL2.md";

    public static void main(String[] args) {
        if (args.length ==2){
            toolsPath = args[0];
            modelPath = args[1];
        }

        var model = loadModelFile();
        var tools = ProVerBMateApplication.demoTime ? loadDemo() : loadTools();
        Tool2Tool converter;
        OntologyService ontologyService = new OntologyService(model);
        for (File tool : tools){
            converter = new Tool2Tool.Builder(ontologyService, tool)
                    .convertName()
                    .convertRepository()
                    .convertPV()
                    .convertDOIs()
                    .convertAbstract()
                    .build();
            converter.convert();
        }
        try {
            ontologyService.write("extracted_"+model.getName());
        }catch (IOException e){
            System.err.println("Write to file fail");
        }
    }

    private static File loadModelFile(){
        return new File(modelPath);
    }

    private static List<File> loadDemo(){
        var list = new ArrayList<File>(1);
        list.add(new File(toolsPath + "/" + demoTool));
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
            if (file.isFile()){
                newFiles.add(file);
            }
        }
        return newFiles;
    }
}
