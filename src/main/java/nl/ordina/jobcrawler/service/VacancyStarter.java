package nl.ordina.jobcrawler.service;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.scrapers.HuxleyITVacancyScraper;
import nl.ordina.jobcrawler.scrapers.JobBirdScraper;
import nl.ordina.jobcrawler.scrapers.YachtVacancyScraper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/*
This 'starter' class uses the @Scheduled annotation. Every 15 minutes it executes the cronJobSch() function to retrieve all vacancies.
Upon fetching the vacancies it runs a check to verify if the vacancy is already present in the database.
*/

@Slf4j
@Component
public class VacancyStarter {


    @Autowired
    private VacancyService vacancyService;

    private final YachtVacancyScraper yachtVacancyScraper;

    private final HuxleyITVacancyScraper huxleyITVacancyScraper;

    private final JobBirdScraper jobBirdScraper;

    @Autowired
    public VacancyStarter(YachtVacancyScraper yachtVacancyScraper, HuxleyITVacancyScraper huxleyITVacancyScraper, JobBirdScraper jobBirdScraper) {
        this.yachtVacancyScraper = yachtVacancyScraper;
        this.huxleyITVacancyScraper = huxleyITVacancyScraper;
        this.jobBirdScraper = jobBirdScraper;
    }

    @Scheduled(cron = "0 0/15 * * * *")
    public void cronJobSch() throws IOException {
        // This function gets executed every 15 minutes
        log.info("CRON Scheduled");
        scrape();
    }

    public void scrape() throws IOException {
        List<Vacancy> allVacancies = yachtVacancyScraper.getVacancies();
        allVacancies.addAll(jobBirdScraper.getVacancies());
        allVacancies.addAll(huxleyITVacancyScraper.getVacancies());
        int existVacancy = 0;
        int newVacancy = 0;
        for (Vacancy vacancy : allVacancies) {
            try {
                Optional<Vacancy> existCheck = vacancyService.getExistingVacancy(vacancy.getVacancyURL());
                if (existCheck.isPresent()) {
                    existVacancy++;
                } else {
                    vacancyService.add(vacancy);
                    newVacancy++;
                }
            } catch (IncorrectResultSizeDataAccessException ie) {
                log.error("Record exists multiple times in database already!");
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        log.info(newVacancy + " new vacancies added.");
        log.info(existVacancy + " existing vacancies found.");
        log.info("Finished scraping");
        allVacancies.clear();
    }

    @Scheduled(cron = "0 * * * * *")
    public void deleteNonExistingVacancies(){
        log.info("Started deleting non-existing jobs");
        List<Vacancy> allVacancies = vacancyService.getAllVacancies();
        List<Vacancy> vacanciesToDelete = allVacancies.stream()
                .filter(vacancy -> !vacancy.checkURL()) //if the url is not good anymore add it in the vacanciesToDelete
                .collect(Collectors.toList());

        log.info(vacanciesToDelete.size() + " vacancy to delete.");

        for(Vacancy vacancyToDelete: vacanciesToDelete){
            vacancyService.delete(vacancyToDelete.getId());
        }
        log.info("Finished deleting non-existing jobs");
    }
}
