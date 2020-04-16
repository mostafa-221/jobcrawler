package nl.ordina.jobcrawler.service;


import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillService implements nl.ordina.jobcrawler.service.Service<Skill> {

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }


    //******** Repository methods, to be changed with DTO layer ********//
    @Override
    public List<Skill> getAll() {
        return skillRepository.findAll();
    }

    @Override
    public Optional<Skill> getById(UUID id) {
        return skillRepository.findById(id);
    }

    public Optional<Skill> getByName(String skillName) {
        return skillRepository.findByName(skillName);
    }

    @Override
    public void deleteById(UUID id) {
        skillRepository.deleteById(id);
    }

    @Override
    public Skill add(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public Skill update(UUID id, Skill newSkill){
        // To be implemented
        return null;
    }

    public int countRelationsById(UUID id){
        return skillRepository.countRelationsById(id);
    }

    public void removeRelationsById(UUID skillId, UUID vacancyId){
        skillRepository.removeRelationsById(skillId, vacancyId);
    }

    public Set<Vacancy> getVacanciesBySkill(String skillName) {
        // searches for a skill, if not found throws a skill not found exception
        Skill skill = getByName(skillName).orElseThrow(() -> new SkillNotFoundException(skillName));
        return skill.getVacancies();
    }


    //******** functional methods, will handle only data integrity when DTO layer is present ********//

    //******** Adding ********//
    // takes skills, links them to existing ones if available and adds them to a vacancy //
    public void addSkillsToVacancy(Set<Skill> skills, Vacancy vacancy) {
        skills = linkToExistingSkills(skills);
        vacancy.addSkills(skills);
    }

    //******** linking ********//
    // links the skills of the vacancy to existing entries in the database //
    // reads from the skill table //
    public Set<Skill> linkToExistingSkills(Set<Skill> skills) {
        Set<Skill> newSkills = new HashSet<>();
        // Todo: try to find a solution to have 1 query per iteration searching and fetching

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = getByName(skill.getName()); //here happens a search query

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
            int relations = countRelationsById(skill.getId());  //counts the entries in the relation table
            System.out.println("** Skill " + skill.getName() + " has " + relations + " relation(s)");
            if (relations == 0) {
                System.out.println("** Deleting skill " + skill.getName());
                deleteById(skill.getId()); // deletes the skill from the skill table
            }
        }
    }

    // takes skills and removes them from a vacancy //
    public void removeSkillsFromVacancy(Set<Skill> skills, Vacancy vacancy) {
        for (Skill skill : skills) {
            // remove the relationships, this can also be done using the vacancyRepository
            removeRelationsById(skill.getId(), vacancy.getId());
        }
        vacancy.removeSkills(skills);
        deleteSkillsIfNoRelations(skills);
    }

    //******** Updating ********//
    // Takes a new set of skills to replace old ones from a vacancy
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

}
