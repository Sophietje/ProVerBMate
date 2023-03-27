package nl.utwente.proverb.converter.r2pconverter.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Article {

    String title;

    String abs;

    String doiURL;

    List<Author> authors;

    public static class Author {

        @Getter
        private String given;

        @Getter
        private String family;

        private String name;

        @Getter
        private @Nullable String email;

        @Getter
        private @Nullable String orcid;

        public String getName() {
            if (given != null && family != null){
                return family + " " +given;
            } else {
                return name;
            }
        }
    }
}
