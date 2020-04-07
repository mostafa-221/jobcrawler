package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SkillService {

    private SkillRepository skillRepository;

    @Autowired
    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Optional<Skill> getExistingSkill(Skill skill) {
        return skillRepository.findByName(skill.getName());
    }

    public Set<Skill> linkToExistingSkills(Set<Skill> skills, Vacancy vacancy) {
        Set<Skill> newSkills = new HashSet<>();
        // Todo: try to find a solution to have 1 query per iteration searching and fetching

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = getExistingSkill(skill); //here happens a search query

            if (existingSkill.isPresent()) {
                System.out.println("** Existing skill: " + existingSkill.get().getName() + "\t" + existingSkill.get().getId());
                newSkills.add(existingSkill.get()); // here happens a fetch query
//                System.out.println("** finished with " + skill.getName());
            } else {
                newSkills.add(skill);
            }
            skill.addVacancy(vacancy);
        }
        return newSkills;
    }

    public void deleteSkillsIfNoRelations(Set<Skill> skillsToDelete) {
        for (Skill skill : skillsToDelete) {
            int relations = skillRepository.countRelationsById(skill.getId());
            System.out.println("** Skill " + skill.getName() + " has " + relations + " relation(s)");
            if (relations == 0) {
                System.out.println("** Deleting skill " + skill.getName());
                skillRepository.deleteById(skill.getId());
            }
        }
    }

    public void addSkillsToVacancy(List<Skill> skills, Vacancy vacancy) {
        Set<Skill> newSkills = new HashSet<>();

        for (Skill skill : skills) {
            System.out.println("** Checking if skill is in database for " + skill.getName());
            Optional<Skill> existingSkill = skillRepository.findByName(skill.getName()); //here happens a search query
            if (existingSkill.isPresent()) {
                System.out.println("** Existing skill: " + existingSkill.get().getName() + "\t" + existingSkill.get().getId());
                vacancy.addSkill(existingSkill.get()); // here happens a fetch query

            } else {
                vacancy.addSkill(skill);
            }
        }

    }

    public void removeSkillsFromVacancy(List<Skill> skills, Vacancy vacancy) {
        for (Skill skill : skills) {
            // remove the relationships
            skillRepository.removeRelationsById(skill.getId(), vacancy.getId());

            // remove the skill if there are no more relationships
            if (skillRepository.countRelationsById(skill.getId()) == 0) {
                skillRepository.deleteById(skill.getId());
            }
        }
        vacancy.removeSkills(skills);
//        deleteSkillsIfNoRelations(skills);
    }

    public Set<Vacancy> getVacanciesBySkill(String skillName) {
        Optional<Skill> skill = skillRepository.findByName(skillName);
        return skill.map(Skill::getVacancies).orElse(null);
    }

    public void updateSkills(Set<Skill> newSkills, Vacancy vacancy) {

        Set<Skill> oldSkills = vacancy.getSkills();

        System.out.println("** new skills: " + newSkills + " and old skills " + oldSkills);

        List<Skill> skillsToAdd = new ArrayList<Skill>(newSkills); // will contain the new skills to be added
        List<Skill> skillsToRemove = new ArrayList<Skill>(oldSkills); // will contain the skills to be deleted

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
