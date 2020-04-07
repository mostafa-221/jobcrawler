package nl.ordina.jobcrawler.service;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repository.SkillRepository;
import nl.ordina.jobcrawler.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class VacancyService {

    private VacancyRepository vacancyRepository;
    private SkillRepository skillRepository;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, SkillRepository skillRepository) {
        this.vacancyRepository = vacancyRepository;
        this.skillRepository = skillRepository;
    }

    public Skill getExistingSkill(Skill skill) {
        return skillRepository.findByName(skill.getName()).orElse(skill);
    }

    //******** Adding ********//
    public Vacancy add(Vacancy vacancy) {

        Set<Skill> skills = vacancy.getSkills();
        Set<Skill> newSkills = new HashSet<>();
        // Todo: try to find a solution to have 1 query per iteration searching and fetching
        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = skillRepository.findByName(skill.getName()); //here happens a search query
            if (existingSkill.isPresent()) {
                System.out.println("** Existing skill: " + existingSkill.get().getName() + "\t" + existingSkill.get().getId());
                newSkills.add(existingSkill.get()); // here happens a fetch query
//                System.out.println("** finished with " + skill.getName());
            } else {
                newSkills.add(skill);
            }
            skill.addVacancy(vacancy);
        }

        vacancy.setSkills(newSkills);

        if (vacancy.checkURL()) return vacancyRepository.saveAndFlush(vacancy);
        else return null;
    }

    //******** Getting ********//
    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public Vacancy getVacancyById(UUID id) {
        return vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
    }


    //******** Deleting ********//
    public void deleteVacancyById(UUID id) {
        Vacancy vacancyToDelete = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));

        Set<Skill> skillsToDelete = vacancyToDelete.getSkills();
        System.out.println("** 1 skills are:" + skillsToDelete);
        vacancyRepository.deleteById(id);
        System.out.println("** 2 skills are:" + skillsToDelete);

        for (Skill skill : skillsToDelete) {
            int relations = skillRepository.countRelationsById(skill.getId());
            System.out.println("** Skill " + skill.getName() + " has " + relations + " relation(s)");
            if (relations == 0) {
                System.out.println("** Deleting skill " + skill.getName());
                skillRepository.deleteById(skill.getId());
            }
        }

    }

    //******** Updating ********//
    public Vacancy replace(UUID id, Vacancy newJob) {
        Vacancy job = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
//        deleteVacancyById(id);
        System.out.println("** Job before modification: \n" + job);

        job.setVacancyURL(newJob.getVacancyURL());
        job.setTitle(newJob.getTitle());
        job.setBroker(newJob.getBroker());
        job.setVacancyNumber(newJob.getVacancyNumber());
        job.setHours(newJob.getHours());
        job.setLocation(newJob.getLocation());
        job.setPostingDate(newJob.getPostingDate());
        job.setAbout(newJob.getAbout());

        Set<Skill> newJobSkills = newJob.getSkills();
        Set<Skill> oldJobSkills = job.getSkills();

        System.out.println("** new skills: " + newJobSkills + " and old skills " + oldJobSkills);

        List<Skill> newJobSkillsResult = new ArrayList<Skill>(newJobSkills); // will contain the new skills to be added
        List<Skill> oldJobSkillsResult = new ArrayList<Skill>(oldJobSkills); // will contain the skills to be deleted

        for (Skill newSkill : newJobSkills) {
            for (Skill oldSkill : oldJobSkills) {
                if (newSkill.getName().equals(oldSkill.getName())) {
                    newJobSkillsResult.remove(newSkill);
                    oldJobSkillsResult.remove(oldSkill);
                }
            }
        }

        System.out.println("** skills to be removed " + oldJobSkillsResult);
        System.out.println("** skills to be added " + newJobSkillsResult);


//        job.removeSkills(oldJobSkillsResult);
        removeSkillsFromVacancy(job, oldJobSkillsResult);
//        job.addSkills(newJobSkillsResult);
        addSkillsToVacancy(job, newJobSkillsResult);
        System.out.println("** Job after modification: \n" + job);
        return vacancyRepository.save(job);
    }

    public void addSkillsToVacancy(Vacancy vacancy, List<Skill> skills){
        Set<Skill> newSkills = new HashSet<>();

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = skillRepository.findByName(skill.getName()); //here happens a search query
            if (existingSkill.isPresent()) {
                System.out.println("** Existing skill: " + existingSkill.get().getName() + "\t" + existingSkill.get().getId());
                existingSkill.get().addVacancy(vacancy);
                vacancy.getSkills().add(existingSkill.get()); // here happens a fetch query
//                System.out.println("** finished with " + skill.getName());
            } else {
                skill.addVacancy(vacancy);
                vacancy.getSkills().add(skill);
            }
        }

    }

    public void removeSkillsFromVacancy(Vacancy vacancy, List<Skill> skills){
        for(Skill skill: skills){
            // remove the relationships
            skillRepository.removeRelationsById(skill.getId(), vacancy.getId()); //gives mistake

            // remove the skill if there are no more relationships
            if(skillRepository.countRelationsById(skill.getId()) == 0){
                skillRepository.deleteById(skill.getId());
            }
        }
        vacancy.removeSkills(skills);
    }

    public Set<Vacancy> getVacanciesBySkill(String skillName) {
        Optional<Skill> skill = skillRepository.findByName(skillName);
        return skill.map(Skill::getVacancies).orElse(null);
    }
}
