package nl.utwente.proverb.converter.r2pconverter;

public interface BaseAnalyzer {

    /**
     * Enrich the original dataset without changing existed data
     */
    void autoEnrichment();

    /**
     * Generate the data based on pre-defined template, some 'illegal' data will be lost.
     */
    void reGeneration();
}
