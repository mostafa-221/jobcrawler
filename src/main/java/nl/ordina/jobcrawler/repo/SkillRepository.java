package nl.ordina.jobcrawler.repo;

import nl.ordina.jobcrawler.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@RepositoryRestResource
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    @Query(value = "delete from vacancy_skills where skill_id in (select id from skill where name = ?1)",
            nativeQuery = true)
    @Transactional
    @Modifying
    void deleteReferencesToSkill(String name);
}
