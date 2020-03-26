package nl.ordina.jobcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VacancyURLs {
    private String url;
    private String hours;

    public VacancyURLs(String url) {
        this.url = url;
    }
}
