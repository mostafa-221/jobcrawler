package nl.ordina.jobcrawler.service;


import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class SkillService implements CRUDService<Skill, UUID> {

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }


    @Override
    public Optional<Skill> findById(UUID id) {
        return skillRepository.findById(id);
    }

    @Override
    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    @Override
    public boolean update(Skill skill) {
        return false;   // TODO: implementing this function
    }

    @Override
    public Skill save(Skill skill) {
        return skillRepository.save(skill);
    }

    @Override
    public boolean delete(UUID id) {
        try {
            skillRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Optional<Skill> getSkillByName(String skillName) {
        return skillRepository.findByName(skillName);
    }


    //******** linking ********//
    // links the skills of the vacancy to existing entries in the database //
    // reads from the skill table //
    public Set<Skill> linkToExistingSkills(Set<Skill> skills) {
        Set<Skill> newSkills = new HashSet<>();
        // Todo: try to find a solution to have 1 query per iteration searching and fetching

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = getSkillByName(skill.getName()); //here happens a search query

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
    public void addSkillsToVacancy(Set<Skill> skills, Vacancy vacancy) {
        ;
        skills = linkToExistingSkills(skills);
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
