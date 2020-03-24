package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Vacancy {

    @GeneratedValue
    @Id
    private UUID id;

//    @NotBlank //will give error if uncommented
    @Column(columnDefinition="text")
//    @JsonProperty(value = "url") //this is to name the json variable 'url'

//    @Lob
    private URL vacancyURL;


    @NotBlank
    private String title;
    private String broker;

    @NotBlank
    private String vacancyNumber;
    private String hours;
    private String location;
    private String postingDate;

    @Column(columnDefinition="text")
    private String about;

    //can also be an entity with one to many association, which is the better approach
    //Not efficient, or not good for performance
    // makes new entry for each job that has that skill
    // when the aanvraag is deleted, the skills associated to it as also deleted
    @ElementCollection
    private List<String> skills;

    public String getVacancyURL() {
        return vacancyURL.toString();
    }

    public void setVacancyURL(String vacancyURL) {
        if( ! vacancyURL.startsWith("https://")) vacancyURL = "https://" + vacancyURL;

        try {
            this.vacancyURL = new URL(vacancyURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {

        String newLine = "\n";
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(vacancyURL.toString() + newLine);
        returnValue.append(title + newLine);
        returnValue.append(broker + newLine);
        returnValue.append(vacancyNumber + newLine);
        returnValue.append(hours + newLine);
        returnValue.append(location + newLine);
        returnValue.append(postingDate + newLine);
        returnValue.append(about + newLine);
        returnValue.append(skills.toString() + newLine + newLine);
        returnValue.append("*****************************************");
        return returnValue.toString();
    }
}
