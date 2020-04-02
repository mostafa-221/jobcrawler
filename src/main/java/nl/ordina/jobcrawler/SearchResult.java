package nl.ordina.jobcrawler;

import nl.ordina.jobcrawler.model.Vacancy;

import java.util.List;

public class SearchResult {

    private final String[] queries = new String[3];
    private final List<Vacancy> vacancies;

    public SearchResult(SearchRequest request, List<Vacancy> vacancies) {
        this.queries[0] = request.getLocation();
        this.queries[1] = request.getDistance();
        this.queries[2] = request.getKeywords();

        this.vacancies = vacancies;
    }

    public String[] getQueries() { return queries; }
    public List<Vacancy> getVacancies() { return  vacancies; }

}
