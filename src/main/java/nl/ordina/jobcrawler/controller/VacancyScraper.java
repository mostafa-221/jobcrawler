package nl.ordina.jobcrawler.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// All vacancy scrapers have the following functions in common

@Slf4j
abstract class VacancyScraper {

    protected String BROKER = "";
    protected String SEARCH_URL = "";
    protected final String SEARCH_TERM = "java";

    private final ConnectionDocumentService connectionDocumentService;

    @Autowired
    public VacancyScraper(ConnectionDocumentService connectionDocumentService) {
        this.connectionDocumentService = connectionDocumentService;
    }

    public List<Vacancy> getVacancies() throws IOException {
        log.info(String.format("%s -- Start scraping", BROKER.toUpperCase()));
        /*
        getVacancies retrieves all vanacyURLs via the getVacancyURLs method and set the various elements of Vacancy below.
         */
        List<Vacancy> vacancies = new ArrayList<>();
        List<VacancyURLs> vacancyURLs = getVacancyURLs();
        for(VacancyURLs vacancyURL : vacancyURLs){
            Document doc = connectionDocumentService.getConnection(vacancyURL.getUrl());
            if(doc != null) {
                Vacancy vacancy = Vacancy.builder()
                        .vacancyURL(vacancyURL.getUrl())
                        .broker(BROKER)
                        .hours(vacancyURL.getHours())
                        .skillSet(new ArrayList<>())
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

    abstract protected void setVacancyTitle(Document doc, Vacancy vacancy);

    abstract protected void setVacancySpecifics(Document doc, Vacancy vacancy);

    abstract protected List<String> getVacancySpecifics(Document doc);

    abstract protected void setVacancyAbout(Document doc, Vacancy vacancy);

    abstract protected void setVacancySkillSet(Document doc, Vacancy vacancy);

}
