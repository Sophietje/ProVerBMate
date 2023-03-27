package nl.utwente.proverb.converter.r2pconverter.converter;

import lombok.Getter;
import nl.utwente.proverb.converter.r2pconverter.mdwriters.ToolMDWriter;
import nl.utwente.proverb.converter.r2pconverter.service.OntologyService;
import org.apache.jena.rdf.model.Resource;

import java.io.File;

public class Tool2Tool implements Converter{

    private final OntologyService ontologyService;

    private final File tool;
    @Getter
    private final boolean loadRelatedPapers;
    @Getter
    private final boolean loadPaperAllAuthors;
    @Getter
    private final boolean loadPaperMainAuthor;
    @Getter
    private final boolean loadRepositories;
    @Getter
    private final boolean loadRepoLastCommitTime;
    @Getter
    private final boolean loadRepoContributors;

    private Tool2Tool(Builder builder){
        this.ontologyService = new OntologyService(builder.modelFile);
        this.tool = builder.tool;
        this.loadRelatedPapers = builder.loadRelatedPapers;
        this.loadPaperAllAuthors = builder.loadPaperAllAuthors;
        this.loadPaperMainAuthor = builder.loadPaperMainAuthor;
        this.loadRepositories = builder.loadRepositories;
        this.loadRepoLastCommitTime = builder.loadRepoLastCommitTime;
        this.loadRepoContributors = builder.loadRepoContributors;
    }

    public static class Builder{

        final File tool;
        final File modelFile;
        boolean loadRelatedPapers = false;
        boolean loadPaperAllAuthors = false;
        boolean loadPaperMainAuthor = false;
        boolean loadRepositories = false;
        boolean loadRepoLastCommitTime = false;

        boolean loadRepoContributors = false;

        public Builder(File modelFile, File tool){
            this.modelFile = modelFile;
            this.tool = tool;
        }

        public Builder loadRelatedPapers(boolean loadRelatedPapers){
            this.loadRelatedPapers = loadRelatedPapers;
            return this;
        }

        public Builder loadRepositories(boolean loadRepositories){
            this.loadRepositories = loadRepositories;
            return this;
        }

        public Builder loadPaperAllAuthors(boolean loadPaperAllAuthors){
            if (loadPaperAllAuthors){
                this.loadRelatedPapers = true;
                this.loadPaperMainAuthor = false;
            }
            this.loadPaperAllAuthors = loadPaperAllAuthors;
            return this;
        }

        public Builder loadPaperMainAuthor(boolean loadPaperMainAuthor) {
            if (loadPaperMainAuthor){
                this.loadRelatedPapers = true;
                this.loadPaperAllAuthors = false;
            }
            this.loadPaperMainAuthor = loadPaperMainAuthor;
            return this;
        }

        public Builder loadRepoLastCommitTime(boolean loadRepoLastCommitTime) {
            if (loadRepoLastCommitTime){
                this.loadRepositories = true;
            }
            this.loadRepoLastCommitTime = loadRepoLastCommitTime;
            return this;
        }

        public Builder loadRepoContributors(boolean loadRepoContributors) {
            if (loadRepoContributors) {
                this.loadRepositories = true;
            }
            this.loadRepoContributors = loadRepoContributors;
            return this;
        }

        public Tool2Tool build(){
            return new Tool2Tool(this);
        }
    }

    @Override
    public void convert() {
        var insName = tool.getName().split(".md")[0].replace(" ","_");
        System.err.println("Converting: "+insName);
        var toolResource = ontologyService.getTool(insName);
        ToolMDWriter writer = new ToolMDWriter(tool, this);

        convertRepos(toolResource, writer);
        convertArticle(toolResource, writer);

        writer.write();
        System.err.println("Done");
    }

    private void convertRepos(Resource toolResource, ToolMDWriter writer) {
        var repos = ontologyService.getRepositories(toolResource);
        writer.convertRepositories(repos);
    }

    private void convertArticle(Resource toolResource, ToolMDWriter writer) {
        var articles = ontologyService.getArticles(toolResource);
        writer.convertArticles(articles);
    }
}
