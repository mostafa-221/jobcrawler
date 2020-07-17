package nl.ordina.jobcrawler;

import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.Setter;
import nl.ordina.jobcrawler.model.Vacancy;

import java.util.List;

@Data
public class SearchResult {

    private List<Vacancy> vacancies;
    private int currentPage;
    private long totalItems;
    private int totalPages;

}
