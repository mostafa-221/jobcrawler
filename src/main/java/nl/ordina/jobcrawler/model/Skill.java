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
//@Data gives StackOverflowError
public class Skill {
    /* this class will be saved in a table called skill
     * */

    @GeneratedValue
    @Id
    private UUID id;

    @Column(nullable = false, unique = false, columnDefinition = "TEXT")
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
