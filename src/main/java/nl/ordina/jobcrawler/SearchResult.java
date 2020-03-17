package nl.ordina.jobcrawler;

public class SearchResult {

    private final String[] texts;
    private final String[] links;

    public SearchResult(String[] texts, String[] links) {
        this.texts = texts;
        this.links = links;
    }

    public String[] getTexts() { return this.texts; }
    public String[] getLinks() { return this.links; }

}
