package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.model.Aanvraag;
import nl.ordina.jobcrawler.repo.AanvraagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AanvraagService {

    @Autowired
    private AanvraagRepository repository;

    public void add(Aanvraag aanvraag) {
        repository.save(aanvraag);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<Aanvraag> getAllJobs() {
        return repository.findAll();
    }

    public List<Aanvraag> getJobsWithSkill(String skill) {
        return repository.findAll().stream()
                .filter(a -> a.getSkillSet().contains(skill))
                .collect(Collectors.toList());
    }
}
