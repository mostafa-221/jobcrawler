package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

//@Data gives StackOverflowError
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

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "skills")
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
