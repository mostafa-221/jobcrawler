package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@CrossOrigin
@RestController
public class VacancyController {

    private final VacancyService vacancyService;

    @Autowired
    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    //******** Adding jobs to database ********//
    @PostMapping("/addJobWithJson")
    public Vacancy addJob(@Valid @RequestBody Vacancy job) {
        return vacancyService.save(job);
    }


    //******** Getting from database ********//
    // getting jobs by ID
    @GetMapping("/getByID/{id}")
    public Vacancy getByID(@PathVariable("id") UUID id) {
        // Retrieve a vacancy by its ID (UUID). If Vacancy is not found it throws a 'VacancyNotFoundException' (HttpStatus.NOT_FOUND).
        Optional<Vacancy> vacancy = vacancyService.findById(id);
        return vacancy.orElseThrow(() -> new VacancyNotFoundException("Vacancy with id: " + id + " not found."));
    }



    // getting all jobs from database
    @GetMapping("/getAllJobs")
    public List<Vacancy> getAllJobs() {
        // Retrieve all vacancies that are available in the database
        return vacancyService.findAll();
    }


    //******** Deleting jobs from database ********//
    // will delete skills that have no reference anymore
    @DeleteMapping("/delete/{id}")
    public void deleteVacancyById(@PathVariable("id") UUID id) {
        // Delete vacancy by id (UUID)
        vacancyService.delete(id);
    }


    //******** Updating ********//
    // takes all the attributes of the new job and puts them in the job of the ID, therefore also updates the skills
    @PutMapping("{id}")
    Vacancy replaceVacancy(@PathVariable UUID id, @RequestBody Vacancy newVacancy) {
        return vacancyService.replace(id, newVacancy);
    }
}
