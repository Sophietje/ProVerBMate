package nl.utwente.proverb.converter.p2rconverter.converter;


import lombok.Getter;
import nl.utwente.proverb.ontology.PROVERB;
import nl.utwente.proverb.converter.p2rconverter.MDToolReader;
import nl.utwente.proverb.converter.p2rconverter.dto.Tool;
import nl.utwente.proverb.converter.p2rconverter.service.OntologyService;
import org.apache.jena.rdf.model.Resource;

import java.io.File;
import java.util.List;

public class Tool2Tool {

    private final OntologyService ontologyService;

    private final File tool;

    @Getter
    private final boolean conName;

    @Getter
    private final boolean conAbs;

    @Getter
    private final boolean conRepositories;

    @Getter
    private final boolean conDOIs;

    @Getter
    private final boolean conPV;

    private Tool2Tool(Builder builder){
        this.ontologyService = builder.ontologyService;
        this.tool = builder.tool;
        this.conName = builder.conName;
        this.conAbs = builder.conAbs;
        this.conDOIs = builder.conDOIs;
        this.conPV = builder.conPV;
        this.conRepositories = builder.conRepositories;
    }

    public static class Builder{

        final File tool;

        final OntologyService ontologyService;

        boolean conName = false;
        boolean conAbs = false;
        boolean conRepositories = false;
        boolean conDOIs = false;
        boolean conPV = false;

        public Builder(OntologyService ontologyService, File tool){
            this.ontologyService = ontologyService;
            this.tool = tool;
        }

        public Builder convertName(){
            this.conName = true;
            return this;
        }

        public Builder convertAbstract(){
            this.conAbs = true;
            return this;
        }

        public Builder convertRepository() {
            this.conRepositories = true;
            return this;
        }

        public Builder convertDOIs() {
            this.conDOIs = true;
            return this;
        }

        public Builder convertPV() {
            this.conPV = true;
            return this;
        }

        public Tool2Tool build() {
            return new Tool2Tool(this);
        }
    }

    public void convert() {
        MDToolReader reader = new MDToolReader();
        var toolDto = reader.read(this.tool);

        var insName = this.tool.getName().split(".md")[0].replace(" ","_");
        System.out.println("Converting: "+ insName);

        //Convert Tool
        var toolResource = ontologyService.createTool(insName);
        this.convertTool(toolResource, toolDto);
        System.out.println("Done");
    }

    private void convertTool(Resource toolResource, Tool tool){

        if (this.conName){
            var name = tool.getName();
            if (name != null){
                ontologyService.addProperty(toolResource, PROVERB.P_NAME, name);
            }
        }

        if (this.conAbs) {
            var abs = tool.getAbstract();
            if (abs != null){
                ontologyService.addProperty(toolResource, PROVERB.P_ABSTRACT, abs);
            }
        }
        if (this.conPV) {
            var pv = tool.getPV();
            if (pv != null){
                var pvResource = ontologyService.getPV(pv);
                ontologyService.addProperty(toolResource, PROVERB.P_CATEGORY, pvResource);
            }
        }

        if (this.conRepositories) {
            convertRepositories(toolResource, tool.getRepositories());
        }

        if (this.conDOIs) {
            convertDOIs(toolResource, tool.getDOIs());
        }
    }

    private void convertRepositories(Resource toolResource, List<String> repos){
        for (String str : repos){
            var repoResource = ontologyService.createRepository(str);
            ontologyService.addProperty(toolResource, PROVERB.P_REPOSITORY, repoResource);
        }
    }

    private void convertDOIs(Resource toolResource, List<String> doIs){
        for (String str : doIs){
            var doiResource = ontologyService.createArticle(str);
            ontologyService.addProperty(toolResource, PROVERB.P_RELATED_PAPER, doiResource);
        }
    }
}
