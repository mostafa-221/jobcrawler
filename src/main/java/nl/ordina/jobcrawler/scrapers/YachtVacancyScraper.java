package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;


@Slf4j
@Component
public class YachtVacancyScraper extends VacancyScraper {

    private static final String SEARCH_URL = "https://www.yacht.nl/vacatures?_hn:type=resource&_hn:ref=r2_r1_r1&&vakgebiedProf=IT";
    private static final String VACANCY_URL_PREFIX = "https://www.yacht.nl/";
    private static final String BROKER = "Yacht";

    @Autowired
    public YachtVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }

    /**
     * This method retrieves all URLs and other available data of the page that shows multiple vacancies.
     * @return List of VacancyURLs with as much details of the vacancy as possible.
     */
    @Override
    protected List<VacancyURLs> getVacancyURLs() {
        //  Returns a List with VacancyURLs
        List<VacancyURLs> vacancyURLs = new ArrayList<>();
        int totalNumberOfPages = 1;
        for (int pageNumber = 1; pageNumber <= totalNumberOfPages; pageNumber++) {
            YachtVacancyResponse yachtVacancyResponse = scrapeVacancies(pageNumber);

            if (pageNumber == 1) {
                totalNumberOfPages = yachtVacancyResponse.getPages();
                log.info("YACHT -- Total number of pages: " + totalNumberOfPages);
            }

            log.info("YACHT -- Retrieving vacancy urls from page: " + yachtVacancyResponse.getCurrentPage() + " of " + yachtVacancyResponse.getPages());
            for (Map<String, Object> vacancyData : yachtVacancyResponse.getVacancies()) {
                Map<String, Object> vacancyMetaData = (Map<String, Object>) vacancyData.get("meta");
                String vacancyURL = (String) vacancyData.get("detailUrl");
                String title = (String) vacancyData.get("title");
                String hours = (String) vacancyMetaData.get("hours");
                String location = (String) vacancyMetaData.get("location");
                String vacancyNumber = (String) vacancyData.get("vacancyNumber");
                String postingDate = (String) vacancyData.get("date");
                vacancyURL =  vacancyURL.contains("?") ? vacancyURL.split("\\?")[0] : vacancyURL;
                vacancyURLs.add(
                        VacancyURLs.builder()
                        .url(VACANCY_URL_PREFIX + vacancyURL)
                        .title(title)
                        .hours(hours)
                        .location(location)
                        .vacancyNumber(vacancyNumber)
                        .postingDate(postingDate)
                        .build()
                );
            }
        }
        return vacancyURLs;
    }

    /**
     * This method does a get request to Yacht to retrieve the vacancies from a specific page.
     * @param pageNumber Pagenumber of which the vacancy data should be retrieved
     * @return json response from the get request
     */
    private YachtVacancyResponse scrapeVacancies(int pageNumber) {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        mappingJackson2HttpMessageConverter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(mappingJackson2HttpMessageConverter);

        ResponseEntity<YachtVacancyResponse> response
                = restTemplate.getForEntity(getSEARCH_URL() + "&pagina=" + pageNumber, YachtVacancyResponse.class);

        return response.getBody();
    }

    @Override
    protected int getTotalNumberOfPages(Document doc) {
        return 1;
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {
        log.info("YACHT -- Scraping: " + vacancy.getTitle());
    }

    @Override
    protected void setVacancySpecifics(Document doc, Vacancy vacancy) {
    }

    @Override
    protected List<String> getVacancySpecifics(Document doc) {
        return new ArrayList<>();
    }

    /**
     * This method selects the vacancy details from the html document
     * @param doc jsoup document of a vacancy
     * @param vacancy vacancy object with as much details as possible so far is passed to this method
     */
    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {
        // Extracts the about part from the vacancy
        Element vacancyBody = doc.select(".rich-text--vacancy").first();
        vacancy.setAbout(vacancyBody.text());
    }

    /**
     * This method tries to select the needed skills for a vacancy. This can look different per vacancy for which a few scenarios are coded which covers most of them.
     * @param doc jsoup document of a vacancy
     * @param vacancy vacancy object with as much details as possible so far is passed to this method
     */
    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {
        // The needed skills for a vacancy in Dutch are named 'Functie-eisen'. We'd like to select these skills, starting from the h2 tag that contains those. Let's select everything after that h2 tag
        Elements skillSets = doc.select("h2:contains(Functie-eisen) ~ *");
        for (Element skillSet : skillSets) {
            // Once again break the loop if we find another h2 tag.
            if ("h2".equals(skillSet.tagName()))
                break;

            // Some vacancies use an unsorted list for the required skills. Some don't. Let's try to select an unsorted list and verify there is one available.
            if (skillSet.select("ul li").size() > 0) {
                Elements skills = skillSet.select("ul li");
                for (Element skill : skills)
                    vacancy.addSkill(skill.text());
            } else {
                if (!skillSet.text().isEmpty()) {
                    if (skillSet.text().startsWith("• ")) {
                        vacancy.addSkill(skillSet.text().substring(2));
                    } else {
                        vacancy.addSkill(skillSet.text());
                    }
                }
            }

        }
    }

}
