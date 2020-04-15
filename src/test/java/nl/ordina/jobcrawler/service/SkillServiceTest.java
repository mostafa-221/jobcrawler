package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class SkillServiceTest {

    private SkillRepository skillRepositoryMock;
    private SkillService skillService;

    private Vacancy vacancy;
    private List<Vacancy> vacanciesDB;  // fake "database" for vacancy
    private String skillName;
    private Skill skill;
    private List<Skill> skillDB;    // fake "database" for skill

    @BeforeEach
    void init() {
        this.skillRepositoryMock = Mockito.mock(SkillRepository.class);
        this.skillService = new SkillService(skillRepositoryMock);

        vacancy = Vacancy.builder() // making a vacancy
                .id(UUID.randomUUID())
                .vacancyURL("example.com")
                .title("job title Example 1")
                .broker("broker Example")
                .vacancyNumber("1")
                .hours("30")
                .location("location example")
                .postingDate("14 April 2020")
                .about("this is a description of the example job")
                .skills(new HashSet<Skill>())
                .build();

        skillName = "example";
        skill = Skill.builder()
                .id(UUID.randomUUID())
                .name(skillName)
                .vacancies(new HashSet<Vacancy>())
                .build();

        vacancy.addSkill(skill);    // making the link between the vacancy and the skill

        skillDB = new ArrayList<Skill>();
        vacanciesDB = new ArrayList<Vacancy>();

        vacanciesDB.add(vacancy);   // putting the vacancy in the fake vacancy "database"
        skillDB.add(skill); // putting the vacancy in the fake skill "database"
    }

    @Test
    void getAllSkills() {
        /* uses from repository methods: findAll */

        Mockito.when(skillRepositoryMock.findAll()).thenReturn(skillDB);
        assertThat(skillService.getAllSkills()).isEqualTo(skillDB);
    }


    @Test
    void getExistingSkillByName() {
        /* uses from repository methods: findByName */
        Mockito.when(skillRepositoryMock.findByName(skillName)).thenReturn(Optional.ofNullable(skill));
        assertThat(skillService.getSkillByName(skill).get()).isEqualTo(skill);
    }

    @Test
    void getNonExistingSkillByName() {
        /* uses from repository methods: findByName */
        Mockito.when(skillRepositoryMock.findByName(skillName)).thenReturn(Optional.empty());
        assertFalse(skillService.getSkillByName(skill).isPresent());
    }

    @Test
    void getVacanciesBySkill() {
        /* uses from repository methods: findByName */
        Mockito.when(skillRepositoryMock.findByName(skillName)).thenReturn(Optional.ofNullable(skill));
        Set<Vacancy> result = skillService.getVacanciesBySkill(skillName);
        assertTrue(result.contains(vacancy)
                && result.size() == 1);
    }

    @Test
    void linkToExistingSkills() {
        /* uses from repository methods: findByName */
        Mockito.when(skillRepositoryMock.findByName(skillName)).thenReturn(Optional.ofNullable(skill));

        // make a new skill that only has the name of the existing skill
        Skill newSkill = Skill.builder()
                .name(skillName)
                .vacancies(new HashSet<Vacancy>())
                .build();
        Set<Skill> newSkills = new HashSet<Skill>();
        newSkills.add(newSkill);

        Set<Skill> result = skillService.linkToExistingSkills(newSkills);

        // check if the result contains the skill saved in the "database", and only that
        assertTrue(result.contains(skill) && result.size() == 1);
    }

    @Test
    void deleteSkillsIfNoRelations() {
        /* uses from repository methods: deleteById, countRelationsById */

        // when the method deleteById gets called with the id of the existing skill, delete that skill from the database
        Mockito.doAnswer(invocationOnMock -> {
            skillDB.remove(skill);
            return null;
        }).when(skillRepositoryMock).deleteById(skill.getId());

        // when the method countRelationsById gets called with the id of the existing skill,
        // return 0 indicating that it has no relations
        Mockito.when(skillRepositoryMock.countRelationsById(skill.getId())).thenReturn(0);

        Set<Skill> skillSet = new HashSet<Skill>();
        skillSet.add(skill);

        skillService.deleteSkillsIfNoRelations(skillSet);

        assertTrue(skillDB.isEmpty());
    }

    @Test
    void addExistingSkillsToVacancy() {
        /* uses from repository methods: findByName */

        // make new skill
        Skill newSkill = Skill.builder()
                .id(UUID.randomUUID())
                .name("skill2")
                .vacancies(new HashSet<Vacancy>())
                .build();
        skillDB.add(newSkill);  // add it to the database

        Set<Skill> skillSet = new HashSet<Skill>();
        skillSet.add(newSkill);

        // when the method findByName gets called with the name of the new skill, return the skill
        Mockito.when(skillRepositoryMock.findByName(newSkill.getName())).thenReturn(Optional.ofNullable(newSkill));

        skillService.addSkillsToVacancy(skillSet, vacancy);
        Set<Skill> newSkills = vacancy.getSkills();

        assertTrue(newSkills.size() == 2
                && newSkills.contains(skill)
                && newSkills.contains(newSkill));

    }

    @Test
    void addNewSkillsToVacancy() {
        /* uses from repository methods: findByName */

        // make new skill
        Skill newSkill = Skill.builder()
                .id(UUID.randomUUID())
                .name("skill2")
                .vacancies(new HashSet<Vacancy>())
                .build();

        Set<Skill> skillSet = new HashSet<Skill>();
        skillSet.add(newSkill);

        Mockito.when(skillRepositoryMock.findByName(newSkill.getName())).thenReturn(Optional.empty());

        skillService.addSkillsToVacancy(skillSet, vacancy);
        Set<Skill> newSkills = vacancy.getSkills();

        assertTrue(newSkills.size() == 2
                && newSkills.contains(skill)
                && newSkills.contains(newSkill));

    }

    @Test
    void removeSkillsFromVacancy() {
        /* uses from repository methods: removeRelationsById, deleteById, countRelationsById */

        Mockito.doAnswer(invocationOnMock -> {
            skillDB.remove(skill);
            return null;
        }).when(skillRepositoryMock).deleteById(skill.getId());
        Mockito.when(skillRepositoryMock.countRelationsById(skill.getId())).thenReturn(0);

        Set<Skill> skillSet = new HashSet<Skill>();
        skillSet.add(skill);

        skillService.removeSkillsFromVacancy(skillSet, vacancy);

        verify(this.skillRepositoryMock, times(1))
                .removeRelationsById(any(UUID.class), any(UUID.class));

        assertTrue(vacancy.getSkills().isEmpty());

    }

    @Test
    void updateSkills_deletingSkill() {
        /* uses from repository: removeRelationsById, countRelationsById, deleteById */

        Mockito.doAnswer(invocationOnMock -> {
            skillDB.remove(skill);
            return null;
        }).when(skillRepositoryMock).deleteById(skill.getId());
        Mockito.when(skillRepositoryMock.countRelationsById(skill.getId())).thenReturn(0);

        Set<Skill> newSkills = new HashSet<Skill>();
        skillService.updateSkills(newSkills, vacancy);

        verify(this.skillRepositoryMock, times(1))
                .removeRelationsById(skill.getId(), vacancy.getId());
        assertTrue(vacancy.getSkills().isEmpty());
        assertTrue(skillDB.isEmpty());
    }

    @Test
    void updateSkills_addExistingSkill() {
        /* uses from repository: saveAll, , countRelationsById, deleteById, findByName */

        Vacancy newVacancy = Vacancy.builder() // making a vacancy
                .id(UUID.randomUUID())
                .vacancyURL("example.com")
                .title("job title Example 1")
                .broker("broker Example")
                .vacancyNumber("1")
                .hours("30")
                .location("location example")
                .postingDate("14 April 2020")
                .about("this is a description of the example job")
                .skills(new HashSet<Skill>())
                .build();

        Skill newSkill = Skill.builder()
                .name(skillName)
                .vacancies(new HashSet<Vacancy>())
                .build();

        Set<Skill> newSkills = new HashSet<Skill>();
        newSkills.add(newSkill);

        Mockito.when(skillRepositoryMock.findByName(newSkill.getName())).thenReturn(Optional.ofNullable(skill));

        skillService.updateSkills(newSkills, newVacancy);

        Set<Skill> result = newVacancy.getSkills();

        assertTrue(result.contains(skill) && result.size() == 1);
    }

    @Test
    void updateSkills_addNewSkill() {
        /* uses from repository: saveAll, , countRelationsById, deleteById, findByName */

        Vacancy newVacancy = Vacancy.builder() // making a vacancy
                .id(UUID.randomUUID())
                .vacancyURL("example.com")
                .title("job title Example 1")
                .broker("broker Example")
                .vacancyNumber("1")
                .hours("30")
                .location("location example")
                .postingDate("14 April 2020")
                .about("this is a description of the example job")
                .skills(new HashSet<Skill>())
                .build();

        Skill newSkill = Skill.builder()
                .name(skillName)
                .vacancies(new HashSet<Vacancy>())
                .build();

        Set<Skill> newSkills = new HashSet<Skill>();
        newSkills.add(newSkill);

        Mockito.when(skillRepositoryMock.findByName(newSkill.getName())).thenReturn(Optional.of(newSkill));

        skillService.updateSkills(newSkills, newVacancy);

        Set<Skill> result = newVacancy.getSkills();

        assertTrue(result.contains(newSkill) && result.size() == 1);
    }
}