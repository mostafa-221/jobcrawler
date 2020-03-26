package nl.ordina.jobcrawler.service;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.controller.MylerVacancyScraper;
import nl.ordina.jobcrawler.controller.YachtVacancyScraper;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.exception.DuplicateRecordFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
This 'starter' class uses the @Scheduled annotation. Every 15 minutes it executes the cronJobSch() function to retrieve all vacancies.
Upon fetching the vacancies it runs a check to verify if the vacancy is already present in the database.
*/

@Slf4j
@Component
public class VacancyStarter {

    @Autowired
    private VacancyService vacancyService;

    private final MylerVacancyScraper mylerVacancyScraper;
    private final YachtVacancyScraper yachtVacancyScraper;

    @Autowired
    public VacancyStarter(MylerVacancyScraper mylerVacancyScraper, YachtVacancyScraper yachtVacancyScraper) {
        this.mylerVacancyScraper = mylerVacancyScraper;
        this.yachtVacancyScraper = yachtVacancyScraper;
    }

    @Scheduled(cron = "0 0/15 * * * *")
    public void cronJobSch() throws IOException {
        // This function gets executed every 15 minutes
        log.info("CRON Scheduled");
        scrape();
    }

    private void scrape() throws IOException {
        List<Vacancy> allVacancies = yachtVacancyScraper.getVacancies();
        allVacancies.addAll(mylerVacancyScraper.getVacancies());
        int existVacancy = 0;
        int newVacancy = 0;
        for(Vacancy vacancy : allVacancies) {
            Vacancy existCheck = vacancyService.doesRecordExist(vacancy.getVacancyURL());
            if(existCheck == null) {
                vacancyService.add(vacancy);
                newVacancy++;
            } else {
                existVacancy++;
            }
        }
        log.info(newVacancy + " new vacancies added.");
        log.info(existVacancy + " existing vacancies found.");
        log.info("Finished scraping");
        allVacancies.clear();
    }
}
