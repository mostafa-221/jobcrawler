package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
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
    public Vacancy add(Vacancy vacancy) {

        Set<Skill> ExistingSkills = skillService.linkToExistingSkills(vacancy.getSkills(), vacancy);
        vacancy.setSkills(ExistingSkills);

        if (vacancy.checkURL()) return vacancyRepository.saveAndFlush(vacancy);
        else return null;
    }

    //******** Getting ********//
    public List<Vacancy> getAllJobs() {
        return vacancyRepository.findAll();
    }

    public Optional<Vacancy> getByID(UUID id) {
        return vacancyRepository.findById(id);
    }

    public Set<Vacancy> getJobsWithSkill(String skill) {
        return skillService.getVacanciesBySkill(skill);
    }

    public List<Vacancy> getJobsByBroker(String broker) {
        return vacancyRepository.findByBrokerEquals(broker);
    }

    public Optional<Vacancy> getExistingRecord(String url) {
        return vacancyRepository.findByVacancyURLEquals(url);
    }


    //******** Deleting ********//
    public void delete(UUID id) {
        Vacancy vacancyToDelete = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        Set<Skill> skillsToDelete = new HashSet<Skill>(vacancyToDelete.getSkills());

        vacancyRepository.deleteById(id);
        System.out.println(skillsToDelete);
        skillService.deleteSkillsIfNoRelations(skillsToDelete);

    }


    //******** Updating ********//
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

                    skillService.updateSkills(newJob.getSkills(), job);

                    return vacancyRepository.save(job);
                })
                .orElseThrow(() -> new VacancyNotFoundException(id));

    }


}
