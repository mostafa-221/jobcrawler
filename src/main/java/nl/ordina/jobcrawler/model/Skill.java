package nl.ordina.jobcrawler.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Skill {

    @GeneratedValue
    @Id
    private UUID id;

    @NaturalId
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "skills")
    // @ManyToMany(mappedBy = "skills", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
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
    public String toString() {
        return "" + this.name;
    }

}
