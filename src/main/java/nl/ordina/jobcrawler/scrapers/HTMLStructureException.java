package nl.ordina.jobcrawler.scrapers;

public class HTMLStructureException extends Exception {
    public HTMLStructureException(String s) {
        super("HTML structure altered:" + s);
    }
}