package nl.ordina.jobcrawler.model;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Builder;
=======
>>>>>>> 4561687271204b561ceab1017eb6c710337586f2
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
<<<<<<< HEAD
@Builder
@AllArgsConstructor
=======
>>>>>>> 4561687271204b561ceab1017eb6c710337586f2
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
    @ElementCollection
    @Column(columnDefinition = "TEXT")
    private List<String> skillSet;

    public Vacancy() {
        skillSet = new ArrayList<>();
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
        returnValue.append(skillSet + newLine + newLine);
        returnValue.append("*****************************************");
        return returnValue.toString();

    }
}
