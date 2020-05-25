package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

// All vacancy scrapers have the following functions in common

@Slf4j
abstract class VacancyScraper {

    private final String SEARCH_URL;
    private final String BROKER;

    private final ConnectionDocumentService connectionDocumentService;

    public VacancyScraper(@Autowired ConnectionDocumentService connectionDocumentService, String url, String broker) {
        this.connectionDocumentService = connectionDocumentService;
        this.SEARCH_URL = url;
        this.BROKER = broker;
    }

    public String getSEARCH_URL() {
        return SEARCH_URL;
    }

    public List<Vacancy> getVacancies() throws IOException, HTMLStructureException {
        log.info(String.format("%s -- Start scraping", BROKER.toUpperCase()));
        /*
        getVacancies retrieves all vacancyURLs via the getVacancyURLs method and set the various elements of Vacancy below.
         */
        List<Vacancy> vacancies = new ArrayList<>();
        List<VacancyURLs> vacancyURLs = getVacancyURLs();
        for (VacancyURLs vacancyURL : vacancyURLs) {
            Document doc = connectionDocumentService.getConnection(vacancyURL.getUrl());
            if (doc != null) {
                Vacancy vacancy = Vacancy.builder()
                        .vacancyURL(vacancyURL.getUrl())
                        .broker(BROKER)
                        .hours(vacancyURL.getHours())
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

    protected Document getDocument(String url) throws IOException {
        return connectionDocumentService.getConnection(url);
    }

    abstract protected List<VacancyURLs> getVacancyURLs() throws IOException;

    abstract protected int getTotalNumberOfPages(Document doc);

    abstract protected void setVacancyTitle(Document doc, Vacancy vacancy) throws HTMLStructureException;

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

    abstract protected void setVacancyAbout(Document doc, Vacancy vacancy) throws HTMLStructureException;

    abstract protected void setVacancySkillSet(Document doc, Vacancy vacancy);

}
