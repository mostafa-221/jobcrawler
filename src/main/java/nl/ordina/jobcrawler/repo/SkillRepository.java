package nl.ordina.jobcrawler.repo;

import nl.ordina.jobcrawler.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByName(String name);    // Spring makes the query automatically

    @Query(value = "SELECT COUNT(*) FROM vacancy_skills vs WHERE vs.skill_id=?1", nativeQuery = true)
    int countRelationsById(UUID id);

    @Query(value = "DELETE FROM vacancy_skills vs WHERE vs.vacancy_id=?2 AND skill_id=?1", nativeQuery = true)
    @Transactional
    @Modifying
    void removeRelationsById(UUID skillID, UUID jobID);

}
