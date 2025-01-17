package nl.utwente.proverb.service.impl;

import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import nl.utwente.proverb.domain.dto.evaluate.QueryResult;
import nl.utwente.proverb.service.OntologyService;
import nl.utwente.proverb.ontology.PROVERB;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Log4j2
@Service
public class OntologyServiceImpl implements OntologyService {

    @javax.annotation.Resource
    private Model model;

    @Override
    public void addProperty(Resource resource, Property property, @Nullable String literal) {
        if (literal != null && !literal.isBlank()){
            resource.addProperty(property, literal);
        }
    }

    @Override
    public void addProperty(Resource resource, Property property, @NonNull Resource object) {
        resource.addProperty(property, object);
    }

    @Override
    public void addSameAs(Resource domain, Resource range) {
        domain.addProperty(OWL.sameAs, range);
    }

    @Override
    public Resource createContributor(String githubHTMLURL, Resource gitHubResource){
        var person = model.createResource(githubHTMLURL);
        this.addProperty(person, RDF.type, PROVERB.R_CODE_CONTRIBUTOR);
        this.addProperty(gitHubResource, PROVERB.P_CONTRIBUTOR, person);
        return person;
    }

    @Override
    public Resource createWriter(Resource articleResource, String name){
        var writer = model.createResource(PROVERB.getURI() + escapeURIName(name));
        this.addProperty(writer, RDF.type, PROVERB.R_WRITER);
        this.addProperty(articleResource, PROVERB.P_AUTHOR, writer);
        return writer;
    }

    @Override
    public List<Resource> getAllRepositories() {
        var p = model.listSubjectsWithProperty(RDF.type, PROVERB.R_REPOSITORY);
        return p.toList();
    }

    @Override
    public List<Resource> getAllArticles() {
        var p = model.listSubjectsWithProperty(RDF.type, PROVERB.R_ARTICLE);
        return p.toList();
    }

    @Override
    public QueryResult executeSPARQLQuery(Query query) {
        QueryResult queryResult = new QueryResult();
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            var results = qexec.execSelect();

            /*Handle query result*/
            //Set property
            queryResult.setProperties(results.getResultVars());
            //Set value
            var proNum = queryResult.getProperties().size();
            var rowNum = results.getRowNumber();
            //Get columns
            Map<String, List<RDFNode>> values = new HashMap<>(proNum);
            for (String property : queryResult.getProperties()){
                values.put(property ,new ArrayList<>(rowNum));
            }
            //Set the values to corresponding column
            while (results.hasNext()){
                QuerySolution solution = results.nextSolution();
                for (Iterator<String> it = solution.varNames(); it.hasNext();) {
                    String name = it.next();
                    values.get(name).add(solution.get(name));
                }
            }
            queryResult.setValues(values);
        }
        return queryResult;
    }

    @Override
    public void write(String name) throws IOException {
        File outputFile = new File(name);
        if (!outputFile.exists()) {
            outputFile.createNewFile();
        }
        try (var outputStream = new FileOutputStream(outputFile, false)) {
            RDFDataMgr.write(outputStream , model, Lang.RDFXML);
        }
    }

    private String escapeURIName(String str){
        str = str.replace(" ", "_");
        return str;
    }
}
