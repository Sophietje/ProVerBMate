package nl.utwente.proverb.converter.r2pconverter.subanalyzers;

import nl.utwente.proverb.converter.r2pconverter.entities.Paper;
import nl.utwente.proverb.converter.r2pconverter.mdparser.MDTool;
import nl.utwente.proverb.converter.r2pconverter.mdparser.MDToolTemplate;
import nl.utwente.proverb.converter.r2pconverter.model.PVBModel;
import nl.utwente.proverb.ontology.PROVERB;
import nl.utwente.proverb.converter.r2pconverter.BaseAnalyzer;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

import java.util.ArrayList;
import java.util.List;

public class PaperAnalyzer implements BaseAnalyzer {

    private final Resource tool;

    private final PVBModel model;

    private final List<String> papers;

    public PaperAnalyzer(PVBModel model, MDTool mdTool){
        this.model = model;
        this.tool = model.getTool(mdTool.getName());
        this.papers = mdTool.getProperty(MDToolTemplate.PAPERS);
    }

    @Override
    public void autoEnrichment() {
        var entities = getPaperEntities(this.model, this.tool);
        for (int i=0; i< papers.size(); i++) {
            var paper = papers.get(i);
            var paperEntity = entities.stream().filter(entity -> entity.getDoiURL().equals(paper)).findFirst();
            if (paperEntity.isPresent()){
                papers.set(i, enrichTitle(paper, paperEntity.get().getTitle(), paperEntity.get().getDoiURL()));
            }
        }
    }

    @Override
    public void reGeneration() {
        throw new UnsupportedOperationException();
    }

    private String enrichTitle(String origin, String title, String doi) {
        if (containsTitle(origin)){
            return origin;
        }
        String newTitle = "["+title+"]"+"("+doi+")";
        return origin.replace(doi, newTitle);
    }

    private static List<RDFNode> getPaperNodes(PVBModel model, Resource tool) {
        return model.getProperties(tool, PROVERB.P_RELATED_PAPER);
    }

    public static List<Paper> getPaperEntities(PVBModel model, Resource tool) {
        var nodes = getPaperNodes(model, tool);
        var articles = new ArrayList<Paper>(nodes.size());
        for (var node : nodes){
            var dto = new Paper();
            dto.setDoiURL(node.toString());
            model.getProperty((Resource) node, PROVERB.P_NAME).ifPresent(dto::setTitle);
            articles.add(dto);
        }
        return articles;
    }

    private static boolean containsTitle(String str){
        if (str.startsWith("http")){
            return false;
        }
        return str.startsWith("[") && str.contains("]");
    }

    private static boolean containsConference(String str){
        if (!str.contains("'")){
            return false;
        }

        var sp = str.split("'");
        if (sp.length != 2){
            return false;
        }

        try {
            Integer.parseInt(sp[1]);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
}
