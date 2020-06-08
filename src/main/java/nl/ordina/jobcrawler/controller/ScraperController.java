package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.service.VacancyStarter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@CrossOrigin
@RestController
public class ScraperController {

    private final VacancyStarter vacancyStarter;

    @Autowired
    public ScraperController(VacancyStarter vacancyStarter) {
        this.vacancyStarter = vacancyStarter;
    }

    /**
     * start the scraping of jobs
     */
    @PutMapping("/scrape")
    void scrape () {
        /* made in a new thread so that the sender of the request does not have to wait for a response until the
         * scraping is finished.
         */

        Thread newThread = new Thread(vacancyStarter::scrape);
        newThread.start();

    }
}
