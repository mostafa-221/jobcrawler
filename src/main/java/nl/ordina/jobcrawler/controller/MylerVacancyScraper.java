package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
MylerVacancyScraper.java takes care of scraping vacancies from myler.nl
At this moment getVacancies() returns a list with Vacancies that only exist of a vacancyURL and the broker. No details available (yet).
 */

@Component
public class MylerVacancyScraper extends VacancyScraper {

    public MylerVacancyScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, "https://www.myler.nl/opdrachten/?search=java", "Myler");
    }

    @Override
    protected List<VacancyURLs> getVacancyURLs() throws IOException {
        //  Returns a List with VacancyURLs
        List<VacancyURLs> vacancyURLs = new ArrayList<>();
        int totalNumberOfPages = 1;
        for(int pageNumber = 1; pageNumber <= totalNumberOfPages; pageNumber++) {
            Document doc = getDocument(getSEARCH_URL());
            if(doc == null)
                continue;

            if(pageNumber == 1)
                totalNumberOfPages = getTotalNumberOfPages(doc);

            Elements urls = doc.getElementsByClass("headfirst-plugin-item").select("a");
            for(Element url : urls) {
                String vacancyUrl = url.attr("href");
                vacancyURLs.add(new VacancyURLs(vacancyUrl));
//                vacancyURLs.add(VacancyURLs.builder().url(vacancyUrl).build());
            }
        }
        return vacancyURLs;
    }

    @Override
    protected int getTotalNumberOfPages(Document doc) {
        return 0;
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {

    }

    @Override
    protected void setVacancySpecifics(Document doc, Vacancy vacancy) {

    }

    @Override
    protected List<String> getVacancySpecifics(Document doc) {
        return null;
    }

    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {

    }

    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {

    }
}
