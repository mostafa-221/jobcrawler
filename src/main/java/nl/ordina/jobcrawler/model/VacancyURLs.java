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
    private String title;
    private String hours;
    private String location;
    private String vacancyNumber;
    private String postingDate;

    public VacancyURLs(String url) {
        this.url = url;
    }
}
