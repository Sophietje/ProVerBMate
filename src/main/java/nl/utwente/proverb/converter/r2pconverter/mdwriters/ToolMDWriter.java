package nl.utwente.proverb.converter.r2pconverter.mdwriters;


import nl.utwente.proverb.converter.r2pconverter.converter.Tool2Tool;
import nl.utwente.proverb.converter.r2pconverter.dto.Article;
import nl.utwente.proverb.converter.r2pconverter.entities.Repository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class ToolMDWriter {

    private final File filename;

    private final Tool2Tool config;


    public ToolMDWriter(File filename, Tool2Tool tool2Tool){
        this.filename = filename;
        this.config = tool2Tool;
    }

    public void write(List<Repository> repos, List<Article> articles){
        // First need to parse the markdown file
        MDTool tool = ToolMDParser.read(this.filename);
        List<String> mdLinks = tool.getProperty(MDToolTemplate.URIS);
        // Replace repository links
//        if (config.isLoadRepositories() && mdLinks != null){
//            tool.setProperty(MDToolTemplate.URIS, convertRepositories(mdLinks, repos));
//        }
        // Add last commit & activity dates for repos
        if (config.isLoadRepoLastCommitTime() && mdLinks != null) {
            List<String> mdText = tool.getProperty(MDToolTemplate.LAST_COMMIT_DATE);
            tool.setProperty(MDToolTemplate.LAST_COMMIT_DATE, addLastCommitAndActivityDate(mdText, repos));
        }
        // Replace article links by name + link
        List<String> mdArticles = tool.getProperty(MDToolTemplate.PAPERS);
        if (config.isLoadRelatedPapers() && mdArticles != null) {
            tool.setProperty(MDToolTemplate.PAPERS, convertArticles(mdArticles, articles));
        }
        // Write result back to markdown file
        try (var pw = new PrintWriter(new FileWriter(this.filename))) {
            String output = convertMDToolToString(tool);
            pw.println(output);
        } catch (IOException e) {
            System.err.println(e.getStackTrace().toString());
        }
    }

    public String convertMDToolToString(MDTool tool) {
        String s = "";
        for (String property: tool.getTitles()) {
            s += property + "\n";
            s += String.join("\n", tool.getProperty(property));
            s += "\n";
        }
        return s.substring(0, s.length()-1); // Ignore final newline
    }

    private List<String> convertRepositories(List<String> markdownText, List<Repository> repositories) {
        Pattern p = Pattern.compile("\\[(?<text>[^\\]]*)\\]\\((?<link>[^\\)]*)\\)"); // Pattern for markdown links
        List<String> result = new ArrayList<>();
        for (String s: markdownText) {
            for (Repository r: repositories) {
                if (s.contains(r.getUrl()) && !p.matcher(s).find()) {
                    s = s.replace(r.getUrl(), MDToolTemplate.urlWithName(r.getUrl(), r.getName()));
                }
            }
            result.add(s);
        }
        return result;
    }

    private List<String> addLastCommitAndActivityDate(List<String> markdownText, List<Repository> repositories) {
        List<String> result = new ArrayList<>();
        if (markdownText != null) {
            for (String line : markdownText) {
                if (!line.isEmpty() && !line.contains("(default branch)") && !line.contains("(last activity)")) {
                    result.add(line); // Will not copy old dates that were automatically added so they can be updated
                }
            }
        }
        for (Repository r: repositories) {
            if (r.getLastCommitDate() != null) {
                if (repositories.size() == 1) {
                    result.add(r.getLastCommitDate() + " (last activity)");
                } else {
                    // Add repository's name to distinguish dates if there are multiple repos
                    result.add(r.getLongName() + ": " + r.getLastCommitDate() + " (last activity)");
                }
            }
        }
        if (result.size() > 0) result.set(result.size()-1, result.get(result.size()-1)+"\n");
        return result;
    }

    private List<String> convertArticles(List<String> markdownText, List<Article> articles) {
        Pattern p = Pattern.compile("\\[(?<text>[^\\]]*)\\]\\((?<link>[^\\)]*)\\)"); // Pattern for markdown links
        List<String> result = new ArrayList<>();
        for (String s: markdownText) {
            for (Article r: articles) {
                if (s.contains(r.getDoiURL()) && !p.matcher(s).find()) {
                    s = s.replace(r.getDoiURL(), MDToolTemplate.urlWithName(r.getDoiURL(), r.getTitle()));
                }
            }
            result.add(s);
        }
        return result;
    }

//    private void convertArticles(List<String> markdownText, List<Article> articles) {
//            StringBuilder builder = new StringBuilder();
//            builder.append(PAPER).append("\n");
//            builder.append("\n");
//            for (var article : articles){
//                builder.append(convertArticle(article));
//            }
//            builder.append("\n");
//            output.append(builder);
//    }
//
//    private String convertArticle(Article article){
//        StringBuilder builder = new StringBuilder();
//        builder.append("- ").append(MDToolTemplate.urlWithName(article.getDoiURL(), article.getTitle())).append("\n");
//        //TODO: authors
//        return builder.toString();
//    }
}
