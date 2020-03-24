package nl.ordina.jobcrawler.repository;

import nl.ordina.jobcrawler.model.Vacancy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VacancyRepository extends JpaRepository<Vacancy, UUID> {
}
