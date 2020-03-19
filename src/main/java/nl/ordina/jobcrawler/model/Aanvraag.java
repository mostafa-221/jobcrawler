package nl.ordina.jobcrawler.model;

import nl.ordina.jobcrawler.service.StringListConverter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Entity
public class Aanvraag {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;
    @NotNull
    private String aanvraagURL;
    @Lob
    private String title;
    @Lob
    private String broker;
    @Lob
    private String aanvraagNummer;
    @Lob
    private String hours;
    @Lob
    private String location;
    @Lob
    private String postingDate;
    @Lob
    private String about;
    @Lob
    @Convert(converter = StringListConverter.class)
    private List<String> skillSet;


    public Aanvraag() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAanvraagURL() {
        return aanvraagURL;
    }

    public void setAanvraagURL(String aanvraagURL) {
        this.aanvraagURL = aanvraagURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getAanvraagNummer() {
        return aanvraagNummer;
    }

    public void setAanvraagNummer(String aanvraagNummer) {
        this.aanvraagNummer = aanvraagNummer;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public List<String> getSkillSet() {
        return skillSet;
    }

    public void setSkillSet(List<String> skillSet) {
        this.skillSet = skillSet;
    }

    @Override
    public String toString() {
        String newLine = "\n";
        StringBuilder returnValue = new StringBuilder();
        returnValue.append(aanvraagURL.toString() + newLine);
        returnValue.append(title + newLine);
        returnValue.append(broker + newLine);
        returnValue.append(aanvraagNummer + newLine);
        returnValue.append(hours + newLine);
        returnValue.append(location + newLine);
        returnValue.append(postingDate + newLine);
        returnValue.append(about + newLine);
        returnValue.append(skillSet + newLine + newLine);
        returnValue.append("*****************************************");
        return returnValue.toString();

    }
}
