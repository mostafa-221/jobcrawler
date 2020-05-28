package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

// All vacancy scrapers have the following functions in common

@Slf4j
abstract class VacancyScraper {

    private final String SEARCH_URL;

    public VacancyScraper(String url) {
        this.SEARCH_URL = url;
    }

    public Document getDocument(final String url) {
        try {
            String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36 ArabotScraper";
            return Jsoup.connect(url).userAgent(userAgent).get();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getSEARCH_URL() {
        return SEARCH_URL;
    }

    abstract protected List<Vacancy> getVacancies();

}
