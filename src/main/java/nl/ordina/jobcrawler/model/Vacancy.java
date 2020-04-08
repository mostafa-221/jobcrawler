package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor @NoArgsConstructor
@Entity
public class Vacancy {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @NotNull
    private String vacancyURL;
    private String title;
    private String broker;
    private String vacancyNumber;
    private String hours;
    private String location;
    private String postingDate;
    @Column(columnDefinition = "TEXT")
    private String about;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "vacancy_skills",
            joinColumns = @JoinColumn(name = "vacancy_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id"))
    @JsonIgnoreProperties("vacancies") // so that when printing a vacancy, it doesn't list all the vacancies of the skill (so no looping)
    Set<Skill> skills;  //a set is a collection that has no duplicates

    public void addSkill(String skillsToBeAdded) {
        Skill skill = new Skill(skillsToBeAdded);
        this.skills.add(skill);
        skill.addVacancy(this);
    }

    public void addSkill(Skill skillsToBeAdded) {
        this.skills.add(skillsToBeAdded);
        skillsToBeAdded.addVacancy(this);
    }

    public void removeSkill(Skill skillsToBeRemoved) {
        this.skills.remove(skillsToBeRemoved);
        skillsToBeRemoved.removeVacancy(this);
    }

    public void addSkills(Set<Skill> skillsToBeAdded) {
        for (Skill skill : skillsToBeAdded) {
            this.skills.add(skill);
            skill.addVacancy(this);
        }
    }

    public void removeSkills(Set<Skill> skillsToBeRemoved) {
        for (Skill skill : skillsToBeRemoved) {
            this.skills.remove(skill);
            skill.removeVacancy(this);
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
