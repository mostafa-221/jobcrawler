package nl.ordina.jobcrawler.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Aanvraag {

	@GeneratedValue
    @Id
	private String id;
    private URL aanvraagURL;
    private String title;
    private String broker;
    private String aanvraagNummer;
    private String hours;
    private String location;
    private String postingDate;
    private String about;
    private List<String> skillSet;


    public Aanvraag() {
        skillSet = new ArrayList<String>();
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
