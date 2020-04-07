package nl.ordina.jobcrawler.repository;

import nl.ordina.jobcrawler.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SkillRepository extends JpaRepository<Skill, UUID> {
    //    @Query(value = "SELECT s FROM skill s WHERE s.name = :name", nativeQuery = true) // query does not work
    Optional<Skill> findByName(String name);    // Spring makes the query automatically

    @Query(value = "SELECT COUNT(*) FROM vacancy_skills vs WHERE vs.skill_id=?1", nativeQuery = true)
    int countRelationsById(UUID id);

    @Query(value = "DELETE FROM vacancy_skills vs WHERE vs.vacancy_id=?2 AND skill_id=?1", nativeQuery = true)
    @Transactional
    @Modifying
    void removeRelationsById(UUID skillID, UUID jobID);

    void deleteByName(String name);

}
