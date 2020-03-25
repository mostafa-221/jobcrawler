package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;


import java.io.IOException;
import java.net.HttpURLConnection;
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

//    @NotBlank
    @Column(columnDefinition="text")
    @JsonDeserialize
    private String vacancyURL;


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

    public Boolean checkURL(){
        if( ! this.vacancyURL.startsWith("http")) this.vacancyURL = "https://" + this.vacancyURL; //adding the protocol, if not present

        URL url;
        HttpURLConnection huc;
        int responseCode = 0;

        try {
            url = new URL(this.vacancyURL);
            huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");   // faster because we it doesn't download the response body
            responseCode = huc.getResponseCode();

            if(responseCode == 200) return true;    //website is good
            else throw new VacancyURLMalformedException(this.vacancyURL, responseCode);
        } catch (IOException e) {
            throw new VacancyURLMalformedException(this.vacancyURL);
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
