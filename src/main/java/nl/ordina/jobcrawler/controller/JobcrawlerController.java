package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.SkillService;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("jobs")
@RestController
public class JobcrawlerController {

    private VacancyService vacancyService;
    private SkillService skillService;

    @Autowired
    public JobcrawlerController(VacancyService vacancyService, SkillService skillService) {
        this.vacancyService = vacancyService;
        this.skillService = skillService;
    }

    //******** Adding ********//
    @PostMapping
    public Vacancy addVacancy(@Valid @RequestBody Vacancy job){
        vacancyService.add(job);
        return job;
    }

    //******** Getting ********//
    @GetMapping
    public List<Vacancy> getAllVacancies(){ //gets the right number of jobs, but with no content
        return vacancyService.getAllVacancies();
    }

    @GetMapping(path = "skills")
    public List<Skill> getAllSkills(){return skillService.getAllSkills();}

    @GetMapping(path = "{id}")
    public Vacancy getVacancyFromPathById(@PathVariable UUID id){
        return vacancyService.getVacancyById(id);
    }

    @GetMapping(path = "skills/{skill}")
    public Set<Vacancy> getVacanciesBySkill(@PathVariable("skill") String skillName){
        return vacancyService.getVacanciesBySkill(skillName);
    }
    //******** Updating ********//
    @PutMapping("{id}")
    Vacancy replaceVacancy(@PathVariable UUID id, @RequestBody Vacancy newVacancy){
        return vacancyService.replace(id, newVacancy);
    }

    //******** Deleting ********//
    @DeleteMapping(path = "{id}")   //works and deletes associated skills
    public void deleteVacancyById(@PathVariable("id") UUID id){
        vacancyService.deleteVacancyById(id);
    }


}