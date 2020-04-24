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

    @Scheduled(cron = "0 0 12,18 * * *") // Runs two times a day. At 12pm and 6pm
    public void scrape() throws IOException {
        log.info("CRON Scheduled -- Scrape vacancies");
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

    @Scheduled(cron = "0 30 11,17 * * *") // Runs two times a day. At 11.30am and 5.30pm.
    public void deleteNonExistingVacancies() {
        log.info("CRON Scheduled -- Started deleting non-existing jobs");
        List<Vacancy> allVacancies = vacancyService.getAllVacancies();
        List<Vacancy> vacanciesToDelete = allVacancies.stream()
                .filter(vacancy -> {
                    if (vacancy.getBroker().equals("Yacht"))
                        return !yachtVacancyScraper.doesVacancyExist(vacancy.getVacancyURL());
                    else
                        return !vacancy.checkURL();
                }) //if the url is not good anymore add it in the vacanciesToDelete
                .collect(Collectors.toList());

        log.info(vacanciesToDelete.size() + " vacancy to delete.");

        for (Vacancy vacancyToDelete : vacanciesToDelete) {
            vacancyService.delete(vacancyToDelete.getId());
        }
        log.info("Finished deleting non-existing jobs");
    }
}
