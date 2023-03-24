package nl.utwente.proverb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ProVerBMateApplication {

    public static void main(String[] args) {
        /**
         * Required args:
         * --spring.profiles.active={{daily/dev/..}}
         * --github-token={{GIT_KEY}} : API Key used to make authenticated Github API requests
         * --springer-key={{SPRINGER_KEY}}: API Key used to make authenticated Springer API requests
         * --cross-ref.mailto={{CROSS_REF_MAIL}}: E-mail that is used to contact you in case of improper use of the API
         */
        ConfigurableApplicationContext context = SpringApplication.run(ProVerBMateApplication.class, args);
        int exitCode = SpringApplication.exit(context, () -> 0);
        System.exit(exitCode);
    }
}
