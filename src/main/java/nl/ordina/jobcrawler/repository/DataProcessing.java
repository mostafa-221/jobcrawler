package nl.ordina.jobcrawler.repository;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.persistence.dto.VacancyDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
public class DataProcessing {

    @Autowired
    private VacancyRepository vacancyRepository;

    public VacancyDTO save(VacancyDTO vacancyToSave){
        Vacancy vacancy = new Vacancy();
        vacancy.setVacancyURL(vacancyToSave.getAanvraagURL());
        vacancyRepository.save(vacancy);

        return vacancyToSave;
    }
}
