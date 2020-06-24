package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
     */
    @Override
    public boolean delete(UUID id) {

        try {
            vacancyRepository.deleteById(id);
            return true;
        } catch (DataAccessException e) {
            return false;
        }

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
