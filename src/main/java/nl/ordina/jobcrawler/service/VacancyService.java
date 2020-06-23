package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class VacancyService implements CRUDService<Vacancy, UUID> {

    private final VacancyRepository vacancyRepository;
    private final SkillService skillService;

    @Autowired
    public VacancyService(VacancyRepository vacancyRepository, SkillService skillService) {
        this.vacancyRepository = vacancyRepository;
        this.skillService = skillService;
    }


    /**
     * Returns the vacancy with the specified id.
     *
     * @param id ID of the vacancy to retrieve.
     * @return An optional of the requested vacancy if found, and an empty optional otherwise.
     */
    @Override
    public Optional<Vacancy> findById(UUID id) {
        return vacancyRepository.findById(id);
    }


    /**
     * Returns all vacancies in the database.
     *
     * @return All vacancies in the database.
     */
    @Override
    public List<Vacancy> findAll() {
        return vacancyRepository.findAll();
    }


    /**
     * Updates the specified vacancy, identified by its id.
     *
     * @param vacancy The vacancy to update.
     * @return True if the update succeeded, otherwise false.
     */
    @Override
    public boolean update(Vacancy vacancy) {
        return false;   //Todo: implementing this function
    }


    /**
     * Saves the specified vacancy to the database.
     *
     * @param vacancy The vacancy to save to the database.
     * @return The saved vacancy.
     * @throws VacancyURLMalformedException if the URL could not be reached.
     */
    @Override
    public Vacancy save(Vacancy vacancy) {

        if (vacancy.hasValidURL()) {    //checking the url, if it is malformed it will throw a VacancyURLMalformedException
            return vacancyRepository.save(vacancy);
        } else {
            throw new VacancyURLMalformedException(vacancy.getVacancyURL());
        }

    }


    /**
     * Deletes the vacancy with the specified id.
     *
     * @param id The id of the vacancy to delete.
     * @return True if the operation was successful, false otherwise.
     * @throws VacancyNotFoundException if the vacancy with the specified id was not found.
     */
    @Override
    public boolean delete(UUID id) {
//        Vacancy vacancyToDelete = vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
//        Set<Skill> skillsToDelete = new HashSet<Skill>(vacancyToDelete.getSkills()); // saves the skills of the vacancy
//
//        skillsToDelete.forEach(skill -> skill.removeVacancy(vacancyToDelete)); // remove all vacancy relations

        try {
            vacancyRepository.deleteById(id); //deletes the vacancy and all relations to the skills
        } catch (Exception e) {
            return false;
        }

//        skillService.deleteSkillsIfNoRelations(skillsToDelete); //deletes the skills if there are no more relations pointing to it
        return true;
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


    public Optional<Vacancy> getExistingVacancy(String url) {
        return vacancyRepository.findByVacancyURLEquals(url);
    }


}
