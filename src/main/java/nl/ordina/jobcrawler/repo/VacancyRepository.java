package nl.ordina.jobcrawler.repo;

import nl.ordina.jobcrawler.model.Vacancy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {

    @Transactional
    Optional<Vacancy> findByVacancyURLEquals(String url);

    @Query(value = "SELECT v.* FROM Vacancy AS v " +
            "JOIN vacancy_skills AS vs ON vs.vacancy_id = v.id " +
            "JOIN skill AS s ON s.id = vs.skill_id WHERE s.name IN (:skills)" , nativeQuery = true)
    Page<Vacancy> findBySkills(@Param("skills") Set<String> skills, Pageable pageable);

    @Query(value = "SELECT v.* FROM Vacancy AS v " +
            "WHERE lower(v.about) LIKE lower(concat('%', :value, '%')) OR lower(v.location) LIKE lower(concat('%', :value, '%')) " +
            "OR lower(v.title) LIKE lower(concat('%', :value, '%'))", nativeQuery = true)
    Page<Vacancy> findByAnyValue(@Param("value") String value, Pageable pageable);



}
