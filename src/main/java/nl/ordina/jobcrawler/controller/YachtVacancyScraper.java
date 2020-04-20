package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import nl.ordina.jobcrawler.service.Utils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
YachtVacancyScraper.java takes care of all java related vacancies on yacht.nl
Most of the code in this class is based on the available Arabot code. Some changes were needed due to (probably) changes on the Yacht website.
 */

@Component
public class YachtVacancyScraper extends VacancyScraper {

    private static final String SEARCH_URL = "https://www.yacht.nl/vacatures?soortdienstverband=Detachering&zoekterm=java";
    private static final String BROKER = "Yacht";

    @Autowired
    public YachtVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }

    @Override
    protected List<VacancyURLs> getVacancyURLs() throws IOException {
        //  Returns a List with VacancyURLs
        List<VacancyURLs> vacancyURLs = new ArrayList<>();
        int totalNumberOfPages = 1;
        for (int pageNumber = 1; pageNumber <= totalNumberOfPages; pageNumber++) {
            Document doc = getDocument(getSEARCH_URL() + "&pagina=" + pageNumber);
            if (doc == null)
                continue;

            if (pageNumber == 1)
                totalNumberOfPages = getTotalNumberOfPages(doc);

            Elements jobLinkElements = doc.select("div.results article h2 a[href]");
            Elements hours = doc.select("div.results article dl");
            for (int i = 0; i < hours.size(); i++) {
                String hour = hours.get(i).select("dd").get(1).text().split(" ")[0];
                String vacancyURL = jobLinkElements.get(i).absUrl("href");
                // Split Yacht vacancy url on questionmark, as the position parameter can change due to new or removed vacancies on the Yacht website. URL will also work without any parameter. Prevents duplicates with slightly different url
                vacancyURL = vacancyURL.contains("?") ? vacancyURL.split("\\?")[0] : vacancyURL;
                vacancyURLs.add(
                        VacancyURLs.builder()
                                .url(vacancyURL)
                                .hours(hour)
                                .build()
                );
            }
        }
        return vacancyURLs;
    }

    @Override
    protected int getTotalNumberOfPages(Document doc) {
        // In case there are a lot of pages, we can easily detect the last page by getting the id from the double arrow that goes to the last page.
        Elements pagesElement = doc.select("li.last.last-short a");
        if (pagesElement.isEmpty()) {
            // If only a few pages are available the double arrow is disabled and does not contain an ID. Now we need to request the latest page via li.pages and select the last entry.
            Elements fewPagesElement = doc.select("li.pages ul li a");
            if (fewPagesElement.isEmpty())
                return 1;

            return Integer.parseInt(fewPagesElement.last().attr("id"));
        } else {
            return Integer.parseInt(pagesElement.attr("id"));
        }
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {
        // Selects vacancy title
        Element vacancyHeader = doc.select("header.cf h1").first();
        if (vacancyHeader != null) {
            vacancy.setTitle(vacancyHeader.text());
        }
    }

    @Override
    protected void setVacancySpecifics(Document doc, Vacancy vacancy) {
        // Sets vacancyNumber, vacancyLocation and publish date.
        List<String> vacancySpecifics = getVacancySpecifics(doc);
        vacancy.setVacancyNumber(vacancySpecifics.get(0).trim());
        vacancy.setLocation(Utils.upperCaseFirstChar(vacancySpecifics.get(1).trim()));

        int sizeOfVacancySpecifics = vacancySpecifics.size();
        if (vacancySpecifics.get(sizeOfVacancySpecifics - 1).contains("publicatiedatum")) {
            vacancy.setPostingDate(vacancySpecifics.get(sizeOfVacancySpecifics - 1).trim().substring(16));
        }
    }

    @Override
    protected List<String> getVacancySpecifics(Document doc) {
        // Request for vacancy specifics. Returns a list.
        Element vacancyHeader = doc.select("header.cf p.meta").first();
        if (vacancyHeader != null) {
            String vacancyHeaderString = vacancyHeader.text();
            String[] vacancySpecifics = vacancyHeaderString.split("\\|");
            return Arrays.asList(vacancySpecifics);
        }
        return new ArrayList<>();
    }

    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {
        // Extracts the about part from the vacancy which starts from the first h2 tag to the second h2 tag.
        Elements aboutElements = doc.select(".description h2 ~ *");
        StringBuilder about = new StringBuilder();
        for (Element aboutElement : aboutElements) {
            // If the tagName() is h2 it means we reached the end of the 'about' part.
            if ("h2".equals(aboutElement.tagName()))
                break;

            about.append(aboutElement.text());
        }
        vacancy.setAbout(about.toString());
    }

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
