package nl.ordina.jobcrawler.service;

import nl.ordina.jobcrawler.controller.MylerVacancyScraper;
import nl.ordina.jobcrawler.controller.YachtVacancyScraper;
import nl.ordina.jobcrawler.model.Vacancy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/*
This 'starter' class uses the @Scheduled annotation. Every 15 minutes it executes the cronJobSch() function to retrieve all vacancies.
Upon fetching the vacancies it runs a check to verify if the vacancy is already present in the database.
*/

@Component
public class VacancyStarter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

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
        scrapeYacht();
        //Disbale Myler as it does not scrape entire vacancy yet.
//        scrapeMyler();
        System.out.println();
    }

    private void scrapeYacht() throws IOException {
        System.out.println();
        log.info("YACHT -- Start scraping");
        List<Vacancy> yachtVacancies = yachtVacancyScraper.getVacancies();
        int existVacancy = 0;
        int newVacancy = 0;
        for(Vacancy yachtVacancy : yachtVacancies) {
            Vacancy existCheck = vacancyService.doesRecordExist(yachtVacancy.getVacancyURL());
            if(existCheck == null) {
                vacancyService.add(yachtVacancy);
                newVacancy++;
            } else {
                existVacancy++;
            }
        }
        log.info("YACHT -- " + newVacancy + " new vacancies added.");
        log.info("YACHT -- " + existVacancy + " existing vacancies found.");
        log.info("YACHT -- Finished scraping");
        yachtVacancies.clear(); // Clear List<Vacancy> for next time use
    }

    private void scrapeMyler() throws IOException {
        System.out.println();
        log.info("MYLER -- Start scraping");
        List<Vacancy> mylerVacancies = mylerVacancyScraper.getVacancies();
        int existVacancy = 0;
        int newVacancy = 0;
        for(Vacancy mylerVacany : mylerVacancies) {
            Vacancy existCheck = vacancyService.doesRecordExist(mylerVacany.getVacancyURL());
            if(existCheck == null) {
                vacancyService.add(mylerVacany);
                newVacancy++;
            } else {
                existVacancy++;
            }
        }
        log.info("MYLER -- " + newVacancy + " new vacancies added.");
        log.info("MYLER -- " + existVacancy + " existing vacancies found.");
        log.info("MYLER -- Finished scraping");
        mylerVacancies.clear(); // Clear List<Vacancy> for next time use
    }
}
