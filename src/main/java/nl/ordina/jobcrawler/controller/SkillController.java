package nl.ordina.jobcrawler.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.SkillDTO;
import nl.ordina.jobcrawler.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
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

    @GetMapping(path="getskills")
    public List<SkillDTO> getAllMySkills() {
        List<Skill> skills = skillService.getAllSkills();

        List<SkillDTO> skilldoaList = new ArrayList<>();
        for (Skill s: skills) {
            skilldoaList.add(new SkillDTO(s.getId().toString(), s.getName()));
        }
        return skilldoaList;
    }

    @PostMapping(path="saveskill")
    public void saveskill(@RequestBody SkillDTO skillDTO) {
        log.info("save new skill:" + skillDTO.getName());

        skillService.addSkill(skillDTO.getName());
    }

    @PostMapping(path="deleteskill")
    public void deleteskill(@RequestBody SkillDTO skillDTO) {
        log.info("delete skill:" + skillDTO.getName());
        skillService.deleteSkill(skillDTO.getName());
    }

    // getting all skills from database
    @GetMapping(path = "relinkskills")
    public void relinkSkills() {
        log.info("relink skills");
        skillService.relinkSkills();
    }
}
