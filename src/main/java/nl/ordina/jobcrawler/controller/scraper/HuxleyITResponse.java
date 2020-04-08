package nl.ordina.jobcrawler.controller.scraper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ordina.jobcrawler.model.Vacancy;

import java.util.ArrayList;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HuxleyITResponse {

    private Integer hits;
    private ArrayList<Vacancy> vacancies = new ArrayList<>();

    @JsonProperty("result")
    private void unpackNested(Map<String, Object> result) {
        this.hits = (Integer) result.get("hits");

        ArrayList<Map<String, Object>> vacanciesData = (ArrayList<Map<String, Object>>) result.get("results");

        for(Map<String, Object> vacancyData : vacanciesData) {
            Vacancy vacancy = Vacancy.builder()
                    .vacancyURL((String)vacancyData.get("jobReference"))
                    .vacancyNumber((String)vacancyData.get("jobReference"))
                    .build();

            this.vacancies.add(vacancy);
        }
    }
}
