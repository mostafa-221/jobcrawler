package nl.ordina.jobcrawler.scrapers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class YachtVacancyResponse {

    private Integer currentPage;
    private Integer pages;
    private ArrayList<Map<String, Object>> vacancies;

    private void unpackNested(Map<String, Object> result) {
        this.currentPage = (Integer) result.get("currentPage");
        this.pages = (Integer) result.get("pages");
        this.vacancies = (ArrayList<Map<String, Object>>) result.get("vacancies");
    }
}
