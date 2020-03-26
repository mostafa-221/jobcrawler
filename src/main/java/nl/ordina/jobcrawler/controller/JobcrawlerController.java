package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.SearchRequest;
import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class JobcrawlerController {

    private VacancyService vacancyService;

    @Autowired
    public JobcrawlerController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    // Post mappings

    @PostMapping("/searchrequest")
    public SearchResult searchRequest(@RequestBody SearchRequest request) {

        // Temporary mock data
        String requestText = "For request: " + request.getLocation() + ", " + request.getDistance() + ", " + request.getKeywords() +  ":";
        String[] texts = {requestText, "Test result 1", "Test result 2"};
        String[] links = {"http://google.com/", "http://duckduckgo.com/"};

        return new SearchResult(texts, links);

    }

    @PostMapping("/addJobWithJson")
    public Vacancy addJob(@Valid @RequestBody Vacancy job) {
        return vacancyService.add(job);
    }

    // Get mappings

    @GetMapping("/getByID/{id}")
    public Vacancy getByID(@PathVariable("id") UUID id) {
        // Retrieve a vacancy by its ID (UUID). If Vacancy is not found it throws a 'VacancyNotFoundException' (HttpStatus.NOT_FOUND).
        Optional<Vacancy> vacancy = vacancyService.getByID(id);
        return vacancy.orElseThrow(() -> new VacancyNotFoundException("Vacancy with id: " + id + " not found."));
    }

    @GetMapping("/getJobsByBroker/{broker}")
    public List<Vacancy> getJobsByBroker(@PathVariable("broker") String broker) {
        // Retrieve all vacancies from a specific broker. Currently case sensitive
        return vacancyService.getJobsByBroker(broker);
    }

    @GetMapping("/getAllJobs")
    public List<Vacancy> getAllJobs() {
        // Retrieve all vacancies that are available in the database
        return vacancyService.getAllJobs();
    }

    @GetMapping("/getJobsWithSkill/{skill}")
    public List<Vacancy> getJobsWithSkill(@PathVariable("skill") String skill) {
        // Only show vacancies which needs a specific skill that's requested via a get method
        return vacancyService.getJobsWithSkill(skill);
    }

    // Delete mappings

    @DeleteMapping("/delete/{id}")
    public void deleteAanvraagById(@PathVariable("id") UUID id) {
        // Delete vacancy by id (UUID)
        vacancyService.delete(id);
    }

}
