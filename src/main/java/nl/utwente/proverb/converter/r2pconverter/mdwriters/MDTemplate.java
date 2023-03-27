package nl.utwente.proverb.converter.r2pconverter.mdwriters;

public class MDTemplate {

    private MDTemplate() { }

    public static String urlWithName(String url, String name){
        return "["+name+"]("+url+")";
    }
}
