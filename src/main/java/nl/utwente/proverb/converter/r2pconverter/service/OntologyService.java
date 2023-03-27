package nl.utwente.proverb.converter.r2pconverter.service;

import nl.utwente.proverb.converter.r2pconverter.dto.Article;
import nl.utwente.proverb.converter.r2pconverter.entities.Repository;
import nl.utwente.proverb.ontology.PROVERB;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.RDF;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OntologyService {

    private final Model model;

    public OntologyService(File modelFile){
        this.model = ModelFactory.createDefaultModel().read(modelFile.getAbsolutePath());
    }

    public List<Resource> getAllTools() {
        var p = model.listSubjectsWithProperty(RDF.type, PROVERB.R_TOOL);
        return p.toList();
    }

    public Resource getTool(String insName) {
        return model.getResource(PROVERB.getURI() + insName);
    }


    public List<RDFNode> getRepositoryNodes(Resource toolResource) {

        var p = model.listObjectsOfProperty(toolResource, PROVERB.P_REPOSITORY);
        return p.toList();
    }

    public List<Repository> getRepositories(Resource toolResource) {
        var repositoryNodes = this.getRepositoryNodes(toolResource);
        var repos = new ArrayList<Repository>(repositoryNodes.size());
        for (var node : repositoryNodes){
            var dto = new Repository();
            dto.setUrl(node.toString());
            this.getSingleProperty(node, PROVERB.P_NAME).ifPresent(dto::setName);
            this.getSingleProperty(node, PROVERB.P_LAST_COMMIT_DATE).ifPresent(dto::setLastCommitDate);
            repos.add(dto);
        }
        return repos;
    }

    public List<RDFNode> getArticleNodes(Resource toolResource) {
        var p = model.listObjectsOfProperty(toolResource, PROVERB.P_RELATED_PAPER);
        return p.toList();
    }

    public List<Article> getArticles(Resource toolResource) {
        var nodes = this.getArticleNodes(toolResource);
        var articles = new ArrayList<Article>(nodes.size());
        for (var node : nodes){
            var dto = new Article();
            dto.setDoiURL(node.toString());
            this.getSingleProperty(node, PROVERB.P_NAME).ifPresent(dto::setTitle);
            articles.add(dto);
        }
        return articles;
    }

    public Optional<String> getSingleProperty(RDFNode node, Property proverbProperty){
        var p = model.listObjectsOfProperty((Resource) node, proverbProperty)
                .toList().stream().findFirst();
        return p.map(rdfNode -> rdfNode.asLiteral().getString());
    }
}
