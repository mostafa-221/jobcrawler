package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;


import javax.persistence.*;
import javax.validation.constraints.NotBlank;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Vacancy {

    @GeneratedValue
    @Id
    private UUID id;

    //    @NotBlank
    @Column(columnDefinition = "text")
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

    @Column(columnDefinition = "text")
    private String about;

    //can also be an entity with one to many association, which is the better approach
    //Not efficient, or not good for performance
    // makes new entry for each job that has that skill
    // when the aanvraag is deleted, the skills associated to it as also deleted
//    @ElementCollection
//    private List<String> skills;

    // Eager fetch type so that the skills are loaded when a vacancy is loaded
    // Fetch type lazy is better for many to many relationships
    @ManyToMany(cascade = CascadeType.PERSIST) // is cascading good in this case?
    @JoinTable(
            name = "vacancy_skills",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @JsonIgnoreProperties("vacancies") // so that when printing a vacancy, it doesnt list all the vacancies until the skill
            Set<Skill> skills = new HashSet<>();  //a set is a collection that has no duplicates

    public void addSkills(List<Skill> skillsToBeAdded) {
        for (Skill skill : skillsToBeAdded) {
            this.skills.add(skill);
//            skill.addVacancy(this);
        }
    }

    public void removeSkills(List<Skill> skillsToBeRemoved) {
        for (Skill skill : skillsToBeRemoved) {
            this.skills.remove(skill);
//            skill.removeVacancy(this);
        }
    }


    public Boolean checkURL() {
        if (!this.vacancyURL.startsWith("http"))
            this.vacancyURL = "https://" + this.vacancyURL; //adding the protocol, if not present

        URL url;
        HttpURLConnection huc;
        int responseCode = 0;

        try {
            url = new URL(this.vacancyURL);
            huc = (HttpURLConnection) url.openConnection();
            huc.setRequestMethod("HEAD");   // faster because we it doesn't download the response body
            responseCode = huc.getResponseCode();

            if (responseCode == 200) return true;    //website is good
            else throw new VacancyURLMalformedException(this.vacancyURL, responseCode);
        } catch (IOException e) {
            throw new VacancyURLMalformedException(this.vacancyURL);
        }
    }

    @Override
    public String toString() {

        String newLine = "\n";
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(id.toString() + newLine);
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
