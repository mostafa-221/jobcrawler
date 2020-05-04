package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VacancyService {

    private VacancyRepository vacancyRepository;
    private SkillService skillService;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, SkillService skillService) {
        this.vacancyRepository = vacancyRepository;
        this.skillService = skillService;
    }


    //******** Adding ********//
    // Adding vacancy and accompanying skills and relations to the database //
    public Vacancy add(Vacancy vacancy) {
        //replacing the skills with existing skills from the database if present
        Set<Skill> existingSkills = skillService.linkToExistingSkills(vacancy.getSkills());
        existingSkills.forEach(skill -> skill.addVacancy(vacancy)); // add the vacancy to the skill
        vacancy.setSkills(existingSkills); // add the skills to the vacancy


        if (vacancy.hasValidURL()){    //checking the url, if it is malformed it will throw a VacancyURLMalformedException
            return vacancyRepository.saveAndFlush(vacancy);
        } else {
            throw new VacancyURLMalformedException("Website could not be reached");
        }


    }

    //******** Getting ********//
    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public Optional<Vacancy> getVacancyByID(UUID id) {
        return vacancyRepository.findById(id);
    }

    public Set<Vacancy> getVacanciesBySkill(String skill) {
        return skillService.getVacanciesBySkill(skill);
    }

    public List<Vacancy> getVacanciesByBroker(String broker) {
        return vacancyRepository.findByBrokerEquals(broker);
    }

    public Optional<Vacancy> getExistingVacancy(String url) {
        return vacancyRepository.findByVacancyURLEquals(url);
    }


    //******** Deleting ********//
    // Deletes vacancy and accompanying skills and relations from the database //
    public void delete(UUID id) {
        Vacancy vacancyToDelete = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        Set<Skill> skillsToDelete = new HashSet<Skill>(vacancyToDelete.getSkills()); // saves the skills of the vacancy

        skillsToDelete.forEach(skill -> skill.removeVacancy(vacancyToDelete)); // remove all vacancy relations

        vacancyRepository.deleteById(id); //deletes the vacancy and all relations to the skills

        skillService.deleteSkillsIfNoRelations(skillsToDelete); //deletes the skills if there are no more relations pointing to it
    }


    //******** Updating ********//
    // Updates Vacancy with a given new vacancy (can write to all 3 tables)//
    public Vacancy replace(UUID id, Vacancy newJob) {
        return vacancyRepository.findById(id)
                .map(job -> {
                    System.out.println("** Job before modification: \n" + job);

                    job.setVacancyURL(newJob.getVacancyURL());
                    job.setTitle(newJob.getTitle());
                    job.setBroker(newJob.getBroker());
                    job.setVacancyNumber(newJob.getVacancyNumber());
                    job.setHours(newJob.getHours());
                    job.setLocation(newJob.getLocation());
                    job.setPostingDate(newJob.getPostingDate());
                    job.setAbout(newJob.getAbout());

                    skillService.updateSkills(newJob.getSkills(), job); // updates the skills of the job with the new skills

                    return vacancyRepository.save(job);
                })
                .orElseThrow(() -> new VacancyNotFoundException(id));

    }


}
