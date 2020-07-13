package nl.ordina.jobcrawler.service;


import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/*
    This service operates on the skills in the database
    It contains functions that affect the skill table and the table that relates
    the skill set in the skill table with the vacancies.
 */


@Service
public class SkillService {

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }


    //******** Getting ********//
    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }


    public List<Skill> getAllSkillsByNameAsc() {
        return skillRepository.findByOrderByNameAsc();
    }

    public Optional<Skill> getSkillByName(Skill skill) {
        return skillRepository.findByName(skill.getName());
    }

    public Set<Vacancy> getVacanciesBySkill(String skillName) {
        // searches for a skill, if not found throws a skill not found exception
        Skill skill = skillRepository.findByName(skillName).orElseThrow(() -> new SkillNotFoundException(skillName));
        return skill.getVacancies();
    }


    //******** linking ********//
    // finds the skills of the vacancy in the database
    // add in a list of skills: either the skill of existing entry in the database
    // or the skill in the list of skills if it was not found
    public Set<Skill> retrieveLinksSkills(Set<Skill> skills) {
        Set<Skill> newSkills = new HashSet<>();
        // Todo: try to find a solution to have 1 query per iteration searching and fetching

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = getSkillByName(skill); //here happens a search query

            if (existingSkill.isPresent()) {
                System.out.println("** Existing skill: " + existingSkill.get().getName() + "\t" + existingSkill.get().getId());
                newSkills.add(existingSkill.get()); // here happens a fetch query

            } else {
                newSkills.add(skill);
            }
        }
        return newSkills;
    }


    //******** Deleting ********//
    // Deletes from the skill table if no relations are found in the linking table
    public void deleteSkillsIfNoRelations(Set<Skill> skillsToDelete) {
        for (Skill skill : skillsToDelete) {
            int relations = skillRepository.countRelationsById(skill.getId());  //counts the entries in the relation table
            System.out.println("** Skill " + skill.getName() + " has " + relations + " relation(s)");
            if (relations == 0) {
                System.out.println("** Deleting skill " + skill.getName());
                skillRepository.deleteById(skill.getId()); // deletes the skill from the skill table
            }
        }
    }


    //******** Adding ********//
    // takes skills and adds it to a vacancy //
    public void addSkillsToVacancy(Set<Skill> skills, Vacancy vacancy) { ;
        skills = retrieveLinksSkills(skills);
        vacancy.addSkills(skills);
    }


    //******** Deleting ********//
    // takes skills and removes them from a vacancy //
    public void removeSkillsFromVacancy(Set<Skill> skills, Vacancy vacancy) {
        for (Skill skill : skills) {
            // remove the relationships, this can also be done using the vacancyRepository
            skillRepository.removeRelationsById(skill.getId(), vacancy.getId());
        }
        vacancy.removeSkills(skills);
        deleteSkillsIfNoRelations(skills);
    }


    //******** Updating ********//
    // Takes a new set of skills to replace old ones from a vacancy in the database
    // calculates what needs to be removed and what needs to be added
    // Adds and removes the skills accordingly
    public void updateSkills(Set<Skill> newSkills, Vacancy vacancy) {

        Set<Skill> oldSkills = vacancy.getSkills();

        System.out.println("** new skills: " + newSkills + " and old skills " + oldSkills);

        Set<Skill> skillsToAdd = new HashSet<Skill>(newSkills); // will contain the new skills to be added
        Set<Skill> skillsToRemove = new HashSet<Skill>(oldSkills); // will contain the skills to be deleted

        // Removes all the common skills to only leaves the differences
        for (Skill newSkill : newSkills) {
            for (Skill oldSkill : oldSkills) {
                if (newSkill.getName().equals(oldSkill.getName())) {
                    skillsToAdd.remove(newSkill);
                    skillsToRemove.remove(oldSkill);
                }
            }
        }

        System.out.println("** skills to be removed " + skillsToRemove);
        System.out.println("** skills to be added " + skillsToAdd);

        removeSkillsFromVacancy(skillsToRemove, vacancy);
        skillRepository.saveAll(skillsToAdd);
        addSkillsToVacancy(skillsToAdd, vacancy);
    }

    // add a new skill to the skill table
    public void addSkill(String name) {
        Skill skill = new Skill(name);
        skillRepository.save(skill);
    }

    // delete the skill by name including the link to the skill in the vacancy-skill table
    public void deleteSkill(String name) throws Exception {
        skillRepository.deleteReferencesToSkill(name);
        skillRepository.deleteSkillByName(name);
    }

    // delete all the references to any of the skills
    public void deleteReferencesToSkills() {
        skillRepository.deleteReferencesToSkills();
    }

    // delete all skills in the skill table including the links in the vacancy-skill table
    public void deleteAllSkills() {
        skillRepository.deleteReferencesToSkills();
        skillRepository.deleteSkills();
    }

    // Given a list of skills from the database, add the links to the vacancy-skills table
    // for the given vacancy
    public  void createMatchingSkillLinks(Vacancy vacancy, Set<Skill> matchingSkills) {
        for (Skill skill: matchingSkills) {
            skillRepository.linkSkillToVacancy(vacancy.getId(), skill.getId());
        }
    }


}
