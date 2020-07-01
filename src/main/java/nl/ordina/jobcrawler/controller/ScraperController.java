package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.ScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/*
 * This controller contains the endpoint to start scraping
*/


@CrossOrigin
@RestController
public class ScraperController {

    private final ScraperService scraperService;

    @Autowired
    public ScraperController(ScraperService scraperService) {
        this.scraperService = scraperService;
    }




    /**
     * start the scraping of jobs
     */
    @PutMapping("/scrape")
    void scrape () {
        /* made in a new thread so that the sender of the request does not have to wait for a response until the
         * scraping is finished.
         */

        Thread newThread = new Thread(scraperService::scrape);
        newThread.start();

    }
}
