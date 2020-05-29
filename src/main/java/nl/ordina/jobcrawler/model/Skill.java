package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @GeneratedValue
    @Id
    private UUID id;

    // Skill may not be null, must be unique. Define column as text as varchar is limited to 255 characters. Skill can be a long sentence. Prevent hibernate DataException.
    @Column(nullable = false, unique = false, columnDefinition = "TEXT") // Unique set to false for MVP. Set to true in combination with entitymanager if skill column is still needed at some point.
    private String name;

    @ManyToMany(mappedBy = "skills", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("skills")
    Set<Vacancy> vacancies = new HashSet<>();

    public Skill(String name) {
        this.name = name;
    }

    public void addVacancy(Vacancy vacancy) {
        this.vacancies.add(vacancy);
    }

    public void removeVacancy(Vacancy vacancyToBeRemoved) {
        this.vacancies.remove(vacancyToBeRemoved);
    }

    @Override
    public String toString(){
        return this.name;
    }


}
