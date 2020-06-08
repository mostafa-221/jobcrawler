package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    // getting all skills from database
    @GetMapping(path = "skills")
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

}
