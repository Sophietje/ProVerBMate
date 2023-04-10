package nl.utwente.proverb;

import nl.utwente.proverb.converter.p2rconverter.ProVerB2RDF;
import nl.utwente.proverb.converter.r2pconverter.RDF2ProVerB;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProVerBMateApplication {
    public final static boolean demoTime = true;
    private final static String ontologyFilename = "ProVerB_1.6.0.owl";
    private final static String enrichedOntologyFilename = "enriched_ProVerB_1.6.0.owl";

    private final static String toolDirectory = "../Verification-Tool-Overview/Tools";

    public static void main(String[] args) {
        /**
         * Required args:
         * --spring.profiles.active={{daily/dev/..}}
         * --github-token={{GIT_KEY}} : API Key used to make authenticated Github API requests
         * --springer-key={{SPRINGER_KEY}}: API Key used to make authenticated Springer API requests
         * --cross-ref.mailto={{CROSS_REF_MAIL}}: E-mail that is used to contact you in case of improper use of the API
         */
        String[] mdToRDFArgs = {toolDirectory, ontologyFilename};
        ProVerB2RDF.main(mdToRDFArgs);

        ConfigurableApplicationContext context = SpringApplication.run(ProVerBMateApplication.class, args);
        int exitCode = SpringApplication.exit(context, () -> 0);

        String[] rdfToMDArgs = {enrichedOntologyFilename, toolDirectory};
        RDF2ProVerB.main(rdfToMDArgs);

        System.exit(exitCode);
    }
}
