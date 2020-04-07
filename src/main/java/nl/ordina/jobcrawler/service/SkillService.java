package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repository.SkillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}
