package nl.utwente.proverb.converter.p2rconverter.dto;

import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.utwente.proverb.converter.p2rconverter.util.Pattern;

import java.util.List;


@Setter
@NoArgsConstructor
public class Tool {

    private String name;

    private List<String> urIs;

    private List<String> papers;

    private List<String> meta;

    public String getName() {
        return Pattern.matchName(this.name);
    }

    public List<String> getRepositories() {
        return Pattern.matchGitHubRepositories(this.urIs);
    }

    public List<String> getDOIs() { return Pattern.matchDOIs(this.papers); }

    public String getAbstract() {
        return Pattern.matchAbstract(meta);
    }

    public String getPV() {
        return Pattern.matchPV(meta);
    }
}
