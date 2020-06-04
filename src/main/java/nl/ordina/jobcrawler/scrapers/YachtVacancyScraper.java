package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Slf4j
@Component
public class YachtVacancyScraper extends VacancyScraper {

    private static final String VACANCY_URL_PREFIX = "https://www.yacht.nl";

    @Autowired
    public YachtVacancyScraper() {
        super(
                "https://www.yacht.nl/vacatures?_hn:type=resource&_hn:ref=r2_r1_r1&&vakgebiedProf=IT", // Required search URL. Can be retrieved using getSEARCH_URL()
                "Yacht" // Required broker. Can be retrieved using getBROKER()
        );
    }

    /**
     * This method retrieves all URLs and other available data of the page that shows multiple vacancies.
     *
     * @return List of VacancyURLs with as much details of the vacancy as possible.
     */

    @Override
    public List<Vacancy> getVacancies() {
        log.info(String.format("%s -- Start scraping", getBROKER().toUpperCase()));
        List<Vacancy> vacancies = new ArrayList<>();

        int totalnumberOfPages = 1;
        for (int pageNumber = 1; pageNumber <= totalnumberOfPages; pageNumber++) {
            YachtVacancyResponse yachtVacancyResponse = scrapeVacancies(pageNumber);

            if (pageNumber == 1) {
                totalnumberOfPages = yachtVacancyResponse.getPages();
                log.info(String.format("%s -- Total number of pages: %s", getBROKER(), totalnumberOfPages));
            }

            log.info(String.format("%s -- Retrieving vacancy urls from page: %d of %d", getBROKER(), yachtVacancyResponse.getCurrentPage(), yachtVacancyResponse.getPages()));
            for (Map<String, Object> vacancyData : yachtVacancyResponse.getVacancies()) {
                Map<String, Object> vacancyMetaData = (Map<String, Object>) vacancyData.get("meta");
                String vacancyURL = (String) vacancyData.get("detailUrl");
                vacancyURL = vacancyURL.contains("?") ? vacancyURL.split("\\?")[0] : vacancyURL;
                Document vacancyDoc = getDocument(VACANCY_URL_PREFIX + vacancyURL);
                Vacancy vacancy = Vacancy.builder()
                        .vacancyURL(VACANCY_URL_PREFIX + vacancyURL)
                        .title((String) vacancyData.get("title"))
                        .hours((String) vacancyMetaData.get("hours"))
                        .broker(getBROKER())
                        .vacancyNumber((String) vacancyData.get("vacancyNumber"))
                        .location((String) vacancyMetaData.get("location"))
                        .postingDate((String) vacancyData.get("date"))
                        .about(getVacancyAbout(vacancyDoc))
                        .salary((String) vacancyMetaData.get("salary"))
                        .skills(getSkills(vacancyDoc))
                        .build();

                vacancies.add(vacancy);
                log.info(String.format("%s - Vacancy found: %s", getBROKER(), vacancy.getTitle()));
            }
        }
        log.info(String.format("%s -- Returning scraped vacancies", getBROKER()));
        return vacancies;
    }

    /**
     * This method does a get request to Yacht to retrieve the vacancies from a specific page.
     *
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

    /**
     * This method selects the vacancy details from the html document
     *
     * @param doc jsoup document of a vacancy
     */
    private String getVacancyAbout(Document doc) {
        // Extracts the about part from the vacancy
        Element vacancyBody = doc.select(".rich-text--vacancy").first();
        return vacancyBody.text();
    }

    /**
     * This method tries to select the needed skills for a vacancy. This can look different per vacancy for which a few scenarios are coded which covers most of them.
     *
     * @param doc jsoup document of a vacancy
     */
    private Set<Skill> getSkills(Document doc) {
        Set<Skill> returnSkillSet = new HashSet<>();
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
                    returnSkillSet.add(new Skill(skill.text()));
            } else {
                if (!skillSet.text().isEmpty()) {
                    if (skillSet.text().startsWith("• ")) {
                        returnSkillSet.add(new Skill(skillSet.text().substring(2)));
                    } else {
                        returnSkillSet.add(new Skill(skillSet.text()));
                    }
                }
            }

        }
        return returnSkillSet;
    }

}
