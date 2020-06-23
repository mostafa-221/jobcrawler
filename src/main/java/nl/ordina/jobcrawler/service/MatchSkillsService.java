package nl.ordina.jobcrawler.service;


import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

@Service
public class MatchSkillsService {

    @Autowired
    private SkillService skillService;


    @Autowired
    private SkillRepository skillRepository;


    public boolean matchesSkill(String skillName, Vacancy vacancy) {
        return (vacancy.getAbout().toUpperCase().contains(skillName.toUpperCase()));
    }

    public void changeMatch(Vacancy vacancy) {
        List<Skill> skills = skillService.getAllSkills();
        Set<Skill> matchedSkills = new HashSet<>();

        for (Skill s: skills) {
            System.out.println(vacancy.getTitle());
            if (matchesSkill(s.getName(), vacancy)) {
                System.out.println("matches " + s.getName());
                matchedSkills.add(s);
            } else {
                System.out.println("NO match with " + s.getName());
            }
        }
        vacancy.setSkills(matchedSkills);
        System.out.println(" ");
    } // changeMatch



    private void addStandardSkill(String aSkill) {
        Skill skill = new Skill(aSkill);
        skillRepository.save(skill);
    }

    public void insertStandardSkills() {
//        skillRepository.deleteAll();  // just delete all skills
//        addStandardSkill("AWS");
//        addStandardSkill("SQL");
//        addStandardSkill("Python");
//        addStandardSkill("Docker");
//        addStandardSkill("MySQL");
//        addStandardSkill("Postgres");
    }
}
