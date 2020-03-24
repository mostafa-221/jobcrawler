package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
MylerVacancyScraper.java takes care of scraping vacancies from myler.nl
At this moment getVacancies() returns a list with Vacancies that only exist of a vacancyURL and the broker. No details available (yet).
 */

@Component
public class MylerVacancyScraper implements VacancyScraper {

    private static final String BROKER = "MYLER";
    private static final String SEARCH_TERM = "java";
    private static final String SEARCH_URL = "https://www.myler.nl/opdrachten/?search=" + SEARCH_TERM;

    private List<Vacancy> vacancies = new ArrayList<>();

    private final ConnectionDocumentService connectionDocumentService;

    @Autowired
    public MylerVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        this.connectionDocumentService = connectionDocumentService;
    }

    @Override
    public List<Vacancy> getVacancies() throws IOException {
        /*
        getVacancies connect to the SEARCH_URL and requests this html page.
        If the response is not null it selects the a tags and requests the urls to the vacancy pages.
        Currently this scraper only stores the url and broker into a 'vacancy' object and returns this in a list.
         */
        Document mylerVacancies = connectionDocumentService.getConnection(SEARCH_URL);
        if(mylerVacancies != null) {
            Elements vacancyURLs = mylerVacancies.getElementsByClass("headfirst-plugin-item").select("a");
            for(Element vacancyURL : vacancyURLs) {
                Vacancy vacancy = new Vacancy();
                String row = vacancyURL.attr("href");
                vacancy.setVacancyURL(row);
                vacancy.setBroker("Myler");
                this.vacancies.add(vacancy);
            }
        }
        return vacancies;
    }
}
