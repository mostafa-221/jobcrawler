package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VacancyService extends nl.ordina.jobcrawler.service.Service<Vacancy> {

    private VacancyRepository vacancyRepository;
    private SkillService skillService;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, SkillService skillService) {
        this.vacancyRepository = vacancyRepository;
        this.skillService = skillService;
    }


    //******** Adding ********//
    // Adding vacancy and accompanying skills and relations to the database //
    @Override
    public Vacancy add(Vacancy vacancy) {
        //replacing the skills with existing skills from the database if present
        Set<Skill> existingSkills = skillService.linkToExistingSkills(vacancy.getSkills());
        existingSkills.forEach(skill -> skill.addVacancy(vacancy)); // add the vacancy to the skill
        vacancy.setSkills(existingSkills); // add the skills to the vacancy


        vacancy.checkURL(); //checking the url, if it is malformed it will throw a VacancyURLMalformedException
        return vacancyRepository.saveAndFlush(vacancy); // save and send to the database

    }

    //******** Getting ********//
    @Override
    public List<Vacancy> getAll() {
        return vacancyRepository.findAll();
    }

    @Override
    public Optional<Vacancy> getById(UUID id) {
        return vacancyRepository.findById(id);
    }


    //******** Deleting ********//
    // Deletes vacancy and accompanying skills and relations from the database //
    @Override
    public void deleteById(UUID id) {
        Vacancy vacancyToDelete = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        Set<Skill> skillsToDelete = new HashSet<Skill>(vacancyToDelete.getSkills()); // saves the skills of the vacancy

        skillsToDelete.forEach(skill -> skill.removeVacancy(vacancyToDelete)); // remove all vacancy relations

        vacancyRepository.deleteById(id); //deletes the vacancy and all relations to the skills

        skillService.deleteSkillsIfNoRelations(skillsToDelete); //deletes the skills if there are no more relations pointing to it
    }


    //******** Updating ********//
    // Updates Vacancy with a given new vacancy (can write to all 3 tables)//
    @Override
    public Vacancy update(UUID id, Vacancy newJob) {
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


    //******** methods not implemented from the interface ********//
    public Set<Vacancy> getBySkill(String skill) {
        return skillService.getVacanciesBySkill(skill);
    }

    public List<Vacancy> getByBroker(String broker) {
        return vacancyRepository.findByBrokerEquals(broker);
    }

    public Optional<Vacancy> getByURL(String url) {
        return vacancyRepository.findByVacancyURLEquals(url);
    }



}
