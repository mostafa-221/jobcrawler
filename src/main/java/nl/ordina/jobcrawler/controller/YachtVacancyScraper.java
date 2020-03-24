package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import nl.ordina.jobcrawler.service.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/*
YachtVacancyScraper.java takes care of all java related vacancies on yacht.nl
Most of the code in this class is based on the available Arabot code. Some changes were needed due to (probably) changes on the Yacht website.
 */

@Component
public class YachtVacancyScraper implements VacancyScraper {
    private static final String SEARCH_URL = "https://www.yacht.nl/vacatures?zoekterm=java&soortdienstverband=Detachering";
    
    private List<Vacancy> vacancies = new ArrayList<>();
    
    private final ConnectionDocumentService connectionDocumentService;
    
    @Autowired
    public YachtVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        this.connectionDocumentService = connectionDocumentService;
    }

    @Override
    public List<Vacancy> getVacancies() throws IOException {
        /*
        getVacancies connect to the SEARCH_URL and requests this html page.
        If the response is not null it selects the a tags and requests the urls to the vacancy pages. The 'hours' information is only available on the main page and not on the detailed vacancy pages. Therefore I created as a (temporary) solution as hashmap where I store this data in a key->value storage (url->hours).
         */
        HashMap<String, String> vacancyURLs = getVacancyURLs();
        Iterator vacancyUrl = vacancyURLs.entrySet().iterator();
        while(vacancyUrl.hasNext()){
            Map.Entry pair = (Map.Entry)vacancyUrl.next();
            Document doc = connectionDocumentService.getConnection(pair.getKey().toString());
            if(doc != null) {
                    Vacancy vacancy = new Vacancy();
                    vacancy.setVacancyURL(pair.getKey().toString());
                    vacancy.setBroker("Yacht");
                    vacancy.setHours(pair.getValue().toString());
                    setVacancyTitle(doc, vacancy);
                    setVacancySpecifics(doc, vacancy);
                    setVacancyAbout(doc, vacancy);
                    setVacancySkillSet(doc, vacancy);
                    this.vacancies.add(vacancy);
            }
        }
        return vacancies;
    }

    private HashMap<String, String> getVacancyURLs() throws IOException {
        //  Returns a hashmap with urls and hours a week
        HashMap<String, String> vacancyURLs = new HashMap<>();
        int totalNumberOfPages = 1;
        for(int pageNumber = 1; pageNumber <= totalNumberOfPages; pageNumber++) {
            Document doc = connectionDocumentService.getConnection(SEARCH_URL + "&pagina=" + pageNumber);
            if(doc == null)
                continue;

            Elements jobLinkElements = doc.select("div.results article h2 a[href]");
            Elements hours = doc.select("div.results article dl");
            for(int i = 0; i<hours.size(); i++) {
                Element hour = hours.get(i);
                Element jobLink = jobLinkElements.get(i);
                vacancyURLs.put(jobLink.absUrl("href"), hour.select("dd").get(1).text());
            }
        }
        return vacancyURLs;
    }

    private void setVacancyTitle(Document doc, Vacancy vacancy) {
        // Selects vacancy title
        Element vacancyHeader = doc.select("header.cf h1").first();
        if(vacancyHeader != null) {
            vacancy.setTitle(vacancyHeader.text());
        }
    }

    private void setVacancySpecifics(Document doc, Vacancy vacancy) {
        // Sets vacancyNumber, vacancyLocation and publish date.
        List<String> vacancySpecifics = getVacancySpecifics(doc);
        vacancy.setVacancyNumber(vacancySpecifics.get(0).trim());
        vacancy.setLocation(Utils.upperCaseFirstChar(vacancySpecifics.get(1).trim()));

        int sizeOfVacancySpecifics = vacancySpecifics.size();
        if(vacancySpecifics.get(sizeOfVacancySpecifics-1).contains("publicatiedatum")) {
            vacancy.setPostingDate(vacancySpecifics.get(sizeOfVacancySpecifics-1).trim().substring(16));
        }
    }

    private List<String> getVacancySpecifics(Document doc) {
        // Request for vacancy specifics. Returns a list.
        Element vacancyHeader = doc.select("header.cf p.meta").first();
        if(vacancyHeader != null) {
            String vacancyHeaderString = vacancyHeader.text();
            String[] vacancySpecifics = vacancyHeaderString.split("\\|");
            return Arrays.asList(vacancySpecifics);
        }
        return new ArrayList<>();
    }

    private void setVacancyAbout(Document doc, Vacancy vacancy) {
        // Extracts the about part from the vacancy which starts from the first h2 tag to the second h2 tag.
        Elements aboutElements = doc.select(".description h2 ~ *");
        StringBuilder about = new StringBuilder();
        for(Element aboutElement : aboutElements) {
            // If the tagName() is h2 it means we reached the end of the 'about' part.
            if("h2".equals(aboutElement.tagName()))
                break;

            about.append(aboutElement.text());
        }
        vacancy.setAbout(about.toString());
    }

    private void setVacancySkillSet(Document doc, Vacancy vacancy) {
        // The needed skills for a vacancy in Dutch are named 'Functie-eisen'. We'd like to select these skills, starting from the h2 tag that contains those. Let's select everything after that h2 tag
        Elements skillSets = doc.select("h2:contains(Functie-eisen) ~ *");
        for(Element skillSet : skillSets) {
            // Once again break the loop if we find another h2 tag.
            if("h2".equals(skillSet.tagName()))
                break;

            // Some vacancies use an unsorted list for the required skills. Some don't. Let's try to select an unsorted list and verify there is one available.
            if(skillSet.select("ul li").size() > 0) {
                Elements skills = skillSet.select("ul li");
                for(Element skill : skills)
                    vacancy.getSkillSet().add(skill.text());
            } else {
                if(!skillSet.text().isEmpty()) {
                    if(skillSet.text().startsWith("• ")) {
                        vacancy.getSkillSet().add(skillSet.text().substring(2));
                    } else {
                        vacancy.getSkillSet().add(skillSet.text());
                    }
                }
            }

        }
    }

}
