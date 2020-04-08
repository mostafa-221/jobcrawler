package nl.ordina.jobcrawler.controller.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/*
    Instead of scraping the page HTML for urls, this scraper gets the vacancy data directly from the HuxleyIT API.
 */

@Component
public class HuxleyITVacancyScraper extends VacancyScraper {

    private static final String SEARCH_URL = "https://api.websites.sthree.com/api/services/app/Search/Search";
    private static final String BROKER = "HuxleyIT";

    @Autowired
    public HuxleyITVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }

    @Override
    public List<Vacancy> getVacancies() throws IOException {
        // Get nr of total vacancies
        int totalVacancies = scrapeVacancies(1).getHits();

        // Get all vacancies
        List<Vacancy> vacancies = scrapeVacancies(totalVacancies).getVacancies();

        System.out.println(vacancies);

        return vacancies;
    }

    private HuxleyITResponse scrapeVacancies(int maxNrOfVacancies) {
        RestTemplate restTemplate = new RestTemplate();

        // Configure headers for request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Configure request body parameters
        String[] country = {"Netherlands"};
        Map<String, Object> body = new HashMap<>();
        body.put("resultPage", 0);
        body.put("resultFrom", 0);
        body.put("resultSize", maxNrOfVacancies);
        body.put("language", "en-gb");
        body.put("country", country);
        body.put("brandCode", "HA");

        // Build and trigger the request
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<HuxleyITResponse> response = restTemplate.postForEntity(getSEARCH_URL(), entity, HuxleyITResponse.class);

        // Return the result
        return response.getBody();
    }

    @Override
    protected int getTotalNumberOfPages(Document doc) {
        return 1;
    }

    @Override
    protected List<VacancyURLs> getVacancyURLs() throws IOException {
        return new ArrayList<>();
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {

    }

    @Override
    protected void setVacancySpecifics(Document doc, Vacancy vacancy) {

    }

    @Override
    protected List<String> getVacancySpecifics(Document doc) {
        return new ArrayList<>();
    }

    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {

    }

    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {

    }

}
