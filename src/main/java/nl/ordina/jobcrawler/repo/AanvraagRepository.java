package nl.ordina.jobcrawler.repo;

import nl.ordina.jobcrawler.model.Aanvraag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AanvraagRepository extends JpaRepository<Aanvraag, UUID> {

}
