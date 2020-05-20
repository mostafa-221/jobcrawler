package nl.ordina.jobcrawler;

import nl.ordina.jobcrawler.scrapers.HuxleyITVacancyScraper;
import nl.ordina.jobcrawler.scrapers.JobBirdScraper;
import nl.ordina.jobcrawler.scrapers.YachtVacancyScraper;
import nl.ordina.jobcrawler.service.VacancyStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@SpringBootApplication
@EnableScheduling
public class JobcrawlerApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(JobcrawlerApplication.class, args);

        /**
         * the constructor of the VacancyScraper has all scrapers as a parameter
         * So I had to make an instance of all scrapers to pass them in the constructor
         */

        YachtVacancyScraper yachtVacancyScraper = new YachtVacancyScraper();
        JobBirdScraper jobBirdScraper = new JobBirdScraper();
        HuxleyITVacancyScraper huxleyITVacancyScraper = new HuxleyITVacancyScraper();

        VacancyStarter vacancyStarter = new VacancyStarter(yachtVacancyScraper, huxleyITVacancyScraper, jobBirdScraper);
        vacancyStarter.scrape();
    }

}
