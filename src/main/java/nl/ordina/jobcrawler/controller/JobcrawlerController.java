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

    @Autowired
    private VacancyService vacancyService;

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

    @PostMapping({"/add/{amount}","/add"})
    public int addTest(@PathVariable(name = "amount", required = false) Integer amount) {
        int enteredAmount = amount == null ? 10 : amount;
        // Add x random entries with different skillsets into the database
        for(int i = 0; i<enteredAmount; i++) {
            Vacancy vacancy = new Vacancy();
            vacancy.setVacancyURL("google.nl");
            List<String> skillset = new ArrayList<>();

            for(int j = 0; j < 3; j++) { // add random skills to List<String>
                Random r1 = new Random();
                char c1 = (char) (r1.nextInt(26) + 'a');
                String s1 = String.valueOf(c1);
                skillset.add(s1);
            }
            vacancy.setSkillSet(skillset);

            vacancyService.add(vacancy);
        }
        return enteredAmount;
    }

    // Get mappings

    @GetMapping("/getByID/{id}")
    public Vacancy getByID(@PathVariable("id") UUID id) {
        // Retrieve a vacancy by its ID (UUID). If Vacancy is not found it throws a 'VacancyNotFoundException' (HttpStatus.NOT_FOUND).
        Optional<Vacancy> vacancy = vacancyService.getByID(id);

        if(!vacancy.isPresent())
            throw new VacancyNotFoundException("Vacancy with id: " + id + " not found.");

        return vacancy.get();
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
        // Only show vacancies which needs a specific skill that's requested via a get method (localhost:8080/getJobsWithSkill/requestedskill )
        return vacancyService.getJobsWithSkill(skill);
    }

    // Delete mappings

    @DeleteMapping("/delete/{id}")
    public void deleteAanvraagById(@PathVariable("id") UUID id) {
        // Delete vacancy by id (UUID)
        vacancyService.delete(id);
    }

}