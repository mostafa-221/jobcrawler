package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repo.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class VacancyService {

    @Autowired
    private VacancyRepository repository;

    public Vacancy add(Vacancy vacancy) {
        return repository.save(vacancy);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public List<Vacancy> getAllJobs() {
        return repository.findAll();
    }

    public List<Vacancy> getJobsWithSkill(String skill) {
        return repository.findAll()
                .stream()
                .filter(a -> a.getSkillSet().toString().toLowerCase().contains(skill.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Optional<Vacancy> getByID(UUID id) {
        return repository.findById(id);
    }

    public Vacancy doesRecordExist(String url) {
        List<Vacancy> record = repository.findByVacancyURLEquals(url);
        if(record.size() != 1)
            return null;
        return record.get(0);
    }

    public List<Vacancy> getJobsByBroker(String broker) {
        return repository.findByBrokerEquals(broker);
    }
}
