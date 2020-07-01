package nl.ordina.jobcrawler.service;


import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
*  This service contains the functionality to match skills in the skill table with
*  vacancies in the vacancy table and to add or update relationship records accordingly:
*  It relies on the SkillService to interact with the database.
*
* This service has the following functionality:
*     Boolean matchesSkill(skill name, vacancy)
*         checks whether the vacancy text matches the skill name
*
*     ChangeMatch(vacancy)
*     Change the match with skills for an existing vacancy in the database
*     removes previous relationships with the skill table from this vacancy
*     adds relationships to the skill table for which a matchesSkill holds
*
*
* */

@Slf4j
@Service
public class MatchSkillsService {


    private SkillService skillService;
    private SkillRepository skillRepository;


    @Autowired
    public MatchSkillsService(SkillRepository skillRepository, SkillService skillService) {
        this.skillRepository = skillRepository;
        this.skillService = skillService;
    }

    public boolean matchesSkill(String skillName, Vacancy vacancy) {
        return (vacancy.getAbout().toUpperCase().contains(skillName.toUpperCase()));
    }

    public void changeMatch(Vacancy vacancy) {
        List<Skill> skills = skillService.getAllSkills();
        Set<Skill> matchedSkills = new HashSet<>();

        for (Skill s: skills) {
            if (matchesSkill(s.getName(), vacancy)) {
                matchedSkills.add(s);
            }
        }
        log.info(vacancy.getTitle() + " matched with skills: " + matchedSkills.toString());
        vacancy.setSkills(matchedSkills);
    } 



    private void addStandardSkill(String aSkill) {
        Skill skill = new Skill(aSkill);
        skillService.addSkill(skill);
    }

    //@PostConstruct   //restore this line to create standard set of skills after database cleared
    public void insertStandardSkills() {
        List<Skill> skills = skillService.getAllSkills();
        for (Skill s: skills) {
            skillRepository.deleteReferencesToSkill(s.getName());
        }
        skillRepository.deleteAll();  // just delete all skills
        addStandardSkill("AWS");
        addStandardSkill("SQL");
        addStandardSkill("Python");
        addStandardSkill("Docker");
        addStandardSkill("MySQL");
        addStandardSkill("Postgres");
        addStandardSkill("Java");
        addStandardSkill("JEE");
        addStandardSkill("Angular");
    }
}
