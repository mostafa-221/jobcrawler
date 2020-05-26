package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// All vacancy scrapers have the following functions in common

@Slf4j
abstract class VacancyScraper {

    private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36 ArabotScraper";

    private final String SEARCH_URL;
    private final String BROKER;

    public VacancyScraper(String url, String broker) {
        this.SEARCH_URL = url;
        this.BROKER = broker;
    }

    public static Document getDocument(final String url) throws IOException {
        return Jsoup.connect(url).userAgent(userAgent).get();
    }

    public String getSEARCH_URL() {
        return SEARCH_URL;
    }

    public List<Vacancy> getVacancies() throws IOException {
        log.info(String.format("%s -- Start scraping", BROKER.toUpperCase()));
        /*
        getVacancies retrieves all vacancyURLs via the getVacancyURLs method and set the various elements of Vacancy below.
         */
        List<Vacancy> vacancies = new ArrayList<>();
        List<VacancyURLs> vacancyURLs = getVacancyURLs();
        for (VacancyURLs vacancyURL : vacancyURLs) {
            Document doc = getDocument(vacancyURL.getUrl());
            if (doc != null) {
                Vacancy vacancy = Vacancy.builder()
                        .vacancyURL(vacancyURL.getUrl())
                        .broker(BROKER)
                        .hours(vacancyURL.getHours())
                        .location(vacancyURL.getLocation())
                        .postingDate(vacancyURL.getPostingDate())
                        .vacancyNumber(vacancyURL.getVacancyNumber())
                        .title(vacancyURL.getTitle())
                        .skills(new HashSet<>())
                        .build();
                setVacancyTitle(doc, vacancy);
                setVacancySpecifics(doc, vacancy);
                setVacancyAbout(doc, vacancy);
                setVacancySkillSet(doc, vacancy);
                vacancies.add(vacancy);
            }
        }
        return vacancies;
    }

    abstract protected List<VacancyURLs> getVacancyURLs() throws IOException;

    abstract protected void setVacancyTitle(Document doc, Vacancy vacancy);

    /**************************************************************************
     * Scrapes the
     *    postingdate,
     *    nr of hours (if not set yet from the set of urls)
     *    location (town or municipality) of the vacancy
     * Input:
     *        Doc  - the detail page for the vacancy
     *        Vacancy - the job vacancy corresponging to this page
     * Output:
     *        Sets the corresponding values in the vacancy
     ***************************************************************************/
    abstract protected void setVacancySpecifics(Document doc, Vacancy vacancy);


    /**************************************************************************
     *  Retrieves the relevant portion of the page that contains information
     *  of the specifics
     **************************************************************************/
    abstract protected List<String> getVacancySpecifics(Document doc);

    abstract protected void setVacancyAbout(Document doc, Vacancy vacancy);

    abstract protected void setVacancySkillSet(Document doc, Vacancy vacancy);

}
