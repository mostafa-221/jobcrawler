package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @Autowired
    public VacancyController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    /**
     * Returns all vacancies in the database.
     *
     * @return All vacancies in the database.
     */
    @GetMapping
    public Iterable<Vacancy> getVacancies() {
        return vacancyService.findAll();
    }


    /**
     * Creates a new vacancy.
     *
     * @param vacancy The vacancy to create.
     * @return
     *  The created vacancy and code 201 Created
     *  Code 400 Bad Request if the given body is invalid
     */
    @PostMapping
    public ResponseEntity<Vacancy> createVacancy(@Valid @RequestBody Vacancy vacancy) {
        Vacancy returnedVacancy = vacancyService.save(vacancy);
        try {
            return ResponseEntity
                    .created(new URI("/vacancies/" + returnedVacancy.getId()))
                    .body(returnedVacancy);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    /**
     * Returns the vacancy with the specified ID.
     *
     * @param id The ID of the vacancy to retrieve.
     * @return The vacancy with the specified ID, or code 404 Not Found if the id was not found.
     * @throws VacancyNotFoundException when a vacancy is not found with the specified ID.
     */
    @GetMapping("/{id}")
    public Vacancy getVacancy(@PathVariable UUID id) {
        return vacancyService.findById(id)
                .orElseThrow(() -> new VacancyNotFoundException(id));
    }

    /**
     * Updates the fields in the specified vacancy with the specified ID.
     *
     * @param newVacancy The vacancy field values to update.
     * @param id         The ID of the vacancy to update.
     * @return  A ResponseEntity that contains the updated vacancy or one of the following error statuses:
     *          NOT_FOUND if there is no vacancy in the database with the specified ID
     *          CONFLICT if the eTag does not match the version of the vacancy to update
     */
    @PutMapping("/{id}")
    Vacancy updateVacancy(@PathVariable UUID id, @RequestBody Vacancy newVacancy) {
        vacancyService.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        return vacancyService.replace(id, newVacancy);
    }

    /**
     * Deletes the vacancy with the specified ID.
     *
     * @param id The ID of the vacancy to delete.
     * @return A ResponseEntity with one of the following status codes:
     *  200 OK if the delete was successful
     *  404 Not Found if a vacancy with the specified ID is not found
     */
    @DeleteMapping("/{id}")
    public boolean deleteVacancy(@PathVariable UUID id) {
        vacancyService.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
        return vacancyService.delete(id);
    }


}
