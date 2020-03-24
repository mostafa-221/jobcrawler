package nl.ordina.jobcrawler.persistence.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancyDTO {
    @GeneratedValue
    @Id
    private UUID id;
    private String aanvraagURL;
    private String title;
    private String broker;
    private String aanvraagNummer;
    private String hours;
    private String location;
    private String postingDate;
    private String about;
    private List<String> skillSet;
}
