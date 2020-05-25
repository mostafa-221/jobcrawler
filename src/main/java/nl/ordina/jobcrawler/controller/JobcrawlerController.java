package nl.ordina.jobcrawler.controller;


import nl.ordina.jobcrawler.SearchRequest;
import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.scrapers.HuxleyITVacancyScraper;
import nl.ordina.jobcrawler.scrapers.JobBirdScraper;
import nl.ordina.jobcrawler.scrapers.YachtVacancyScraper;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import nl.ordina.jobcrawler.service.SkillService;
import nl.ordina.jobcrawler.service.VacancyService;
import nl.ordina.jobcrawler.service.VacancyStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;


@CrossOrigin
@RestController
public class JobcrawlerController {

    private VacancyService vacancyService;
    private SkillService skillService;

    @Autowired
    public JobcrawlerController(VacancyService vacancyService, SkillService skillService) {
        this.vacancyService = vacancyService;
        this.skillService = skillService;
    }

    @PostMapping("/searchrequest")
    public SearchResult searchRequest(@RequestBody SearchRequest request) {
        return new SearchResult(request, vacancyService.getAllVacancies());
    }


    //******** Adding jobs to database ********//
    @PostMapping("/addJobWithJson")
    public Vacancy addJob(@Valid @RequestBody Vacancy job) {
        return vacancyService.add(job);
    }


    //******** Getting from database ********//
    // getting jobs by ID
    @GetMapping("/getByID/{id}")
    public Vacancy getByID(@PathVariable("id") UUID id) {
        // Retrieve a vacancy by its ID (UUID). If Vacancy is not found it throws a 'VacancyNotFoundException' (HttpStatus.NOT_FOUND).
        Optional<Vacancy> vacancy = vacancyService.getVacancyByID(id);
        return vacancy.orElseThrow(() -> new VacancyNotFoundException("Vacancy with id: " + id + " not found."));
    }

    @GetMapping("/scrape")
    public void scrape() throws Exception {
        ConnectionDocumentService connectionDocumentService = new ConnectionDocumentService();
         final YachtVacancyScraper yachtVacancyScraper = new YachtVacancyScraper(connectionDocumentService);

         final HuxleyITVacancyScraper huxleyITVacancyScraper = new HuxleyITVacancyScraper(connectionDocumentService);

         final JobBirdScraper jobBirdScraper = new JobBirdScraper(connectionDocumentService);

        VacancyStarter vc = new VacancyStarter(yachtVacancyScraper, huxleyITVacancyScraper, jobBirdScraper);
        vc.scrape();
    }

    // getting jobs by broker
    @GetMapping("/getJobsByBroker/{broker}")
    public List<Vacancy> getJobsByBroker(@PathVariable("broker") String broker) {
        // Retrieve all vacancies from a specific broker. Currently case sensitive
        return vacancyService.getVacanciesByBroker(broker);
    }

    // getting all jobs from database
    @GetMapping("/getAllJobs")
    public List<Vacancy> getAllJobs() {
        // Retrieve all vacancies that are available in the database
        return vacancyService.getAllVacancies();
    }

    // getting all skills from database
    @GetMapping(path = "skills")
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    // getting jobs by skill
    @GetMapping("/getJobsWithSkill/{skill}")
    public Set<Vacancy> getJobsWithSkill(@PathVariable("skill") String skill) {
        // Only show vacancies which needs a specific skill that's requested via a get method
        return vacancyService.getVacanciesBySkill(skill);
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

