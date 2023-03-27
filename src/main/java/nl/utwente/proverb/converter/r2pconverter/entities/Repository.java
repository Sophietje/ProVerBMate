package nl.utwente.proverb.converter.r2pconverter.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class Repository {

    private String name;

    private String abs; // Abstract, i.e. brief description of the repo

    private List<Contributor> contributors;

    private String lastCommitDate;

    private String url;

    @Getter
    @AllArgsConstructor
    public static class Contributor {

        private String name;

        private String url;
    }

    public String getLongName() {
        String copyUrl = url;
        String[] parts;
        if (copyUrl.contains("github.com/")) {
            parts = copyUrl.split("github.com/");
            // E.g. if you have "https://github.com/Sophietje/ProVerBMate" it should return "Sophietje/ProVerBMate"
            return parts[1];
        }
        return getName();
    }
}
