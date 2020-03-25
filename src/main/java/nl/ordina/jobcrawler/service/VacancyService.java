package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.repository.VacancyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VacancyService {
    @Autowired
    private VacancyRepository vacancyRepository;

    //******** Adding ********//
    public Vacancy add(Vacancy vacancy) {
        if(vacancy.checkURL()) return vacancyRepository.saveAndFlush(vacancy);
        else return null;
    }

    //******** Getting ********//
    public List<Vacancy> getAllVacancies() {
        return vacancyRepository.findAll();
    }

    public Vacancy getVacancyById(UUID id) {
        return vacancyRepository.findById(id).orElseThrow(() -> new VacancyNotFoundException(id));
    }


    //******** Deleting ********//
    public void deleteVacancyById(UUID id) {
        vacancyRepository.deleteById(id);
    }

    //******** Updating ********//
    public Vacancy replace(UUID id, Vacancy newJob) {
        return vacancyRepository.findById(id)
                .map(job -> {
                    job.setVacancyURL(newJob.getVacancyURL());
                    job.setTitle(newJob.getTitle());
                    job.setBroker(newJob.getBroker());
                    job.setVacancyNumber(newJob.getVacancyNumber());
                    job.setHours(newJob.getHours());
                    job.setLocation(newJob.getLocation());
                    job.setPostingDate(newJob.getPostingDate());
                    job.setAbout(newJob.getAbout());
                    job.setSkills(newJob.getSkills());

                    return vacancyRepository.save(job);
                })
                .orElseThrow(() -> new VacancyNotFoundException(id));
    }
}
