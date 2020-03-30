package nl.ordina.jobcrawler;

import nl.ordina.jobcrawler.model.Aanvraag;

import java.util.List;

public class SearchResult {

    private final String[] queries = new String[3];
    private final List<Aanvraag> vacancies;

    public SearchResult(SearchRequest request, List<Aanvraag> vacancies) {
        this.queries[0] = request.getLocation();
        this.queries[1] = request.getDistance();
        this.queries[2] = request.getKeywords();

        this.vacancies = vacancies;
    }

    public String[] getQueries() { return queries; }
    public List<Aanvraag> getVacancies() { return  vacancies; }

}
