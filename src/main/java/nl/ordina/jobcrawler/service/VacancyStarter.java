package nl.ordina.jobcrawler.service;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.scrapers.MylerVacancyScraper;
import nl.ordina.jobcrawler.scrapers.YachtVacancyScraper;
import nl.ordina.jobcrawler.model.Vacancy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
            try {
                Optional<Vacancy> existCheck = vacancyService.getExistingRecord(vacancy.getVacancyURL());
                if (existCheck.isPresent()) {
                    existVacancy++;
                } else {
                    vacancyService.add(vacancy);
                    newVacancy++;
                }
            } catch(IncorrectResultSizeDataAccessException ie) {
                log.error("Record exists multiple times in database already!");
            } catch(Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info(newVacancy + " new vacancies added.");
        log.info(existVacancy + " existing vacancies found.");
        log.info("Finished scraping");
        allVacancies.clear();
    }
}
