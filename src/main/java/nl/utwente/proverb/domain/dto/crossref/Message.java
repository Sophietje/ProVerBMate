package nl.utwente.proverb.domain.dto.crossref;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Message {

    private String publisher;

    @JsonAlias("DOI")
    private String doi;

    private String type;

    private Created created;

    private List<String> title;

    private List<Author> author;

    private List<String> subject;
}