package nl.ordina.jobcrawler.scrapers;

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
public class JobBirdScraper extends VacancyScraper {

    private static final String SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java&rad=30&ot=date";
    private static final String BROKER = "Jobbird";

    public JobBirdScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }



    @Override
    protected List<VacancyURLs> getVacancyURLs() throws IOException {
        //  Returns a List with VacancyURLs
       ArrayList<VacancyURLs> URLs = new ArrayList<>();
       Document doc = getDocument(getSEARCH_URL());
       int totalNrOfPages = getTotalNumberOfPages(doc);

       // on each page including this first one are vacancies to be found
        URLs = retrieveVacancyURLsFromDoc(doc);
        return URLs;
    }


    /*
    *    Retrieve the links to the individual pages for each assignment
    */
    private ArrayList<VacancyURLs> retrieveVacancyURLsFromDoc(Document doc) {
        ArrayList<VacancyURLs> result = new ArrayList<>();
        Elements elements = doc.select("div.jobResults");
        Element element = elements.first();
        Element parent = element.parent();
        Elements lijst = parent.children();

        Elements linklijst = new Elements();
        Elements httplinks = lijst.select("a[href]");
        for (Element e: httplinks) {
            String sLink = e.attr("abs:href");
            System.out.println(sLink);
            result.add(new VacancyURLs(sLink));
        }

        return result;
    }

    /* Find the total number of pages, this can be found via the maneuver block at the bottom of the page
     * search for <span elements with class "page-link"
     * from this page link element to the parent of the parent and then look up the children
     * these are <li elements with as attribute value the number of the page
     * continue until the page link with the text "next"
     */
    @Override
    protected int getTotalNumberOfPages(Document doc) {
        Elements elements = doc.select("span.page-link");
        Element parent = elements.first().parent().parent();
        Elements children = parent.children();
        int count = 0;
        for (Element child: children) {
            String text = child.text();
            System.out.println(text);
            if (!text.equalsIgnoreCase("volgende")) count++;
        }
        return count;
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {
        Element vacancyHeader = doc.select("h1.no-margin").first();

        if(vacancyHeader != null) {
            vacancy.setTitle(vacancyHeader.text());
        }
    }

    @Override
    protected void setVacancySpecifics(Document doc, Vacancy vacancy) {
        Elements elements = doc.select("span.job-result__place");
        if (!elements.isEmpty()) {
            Element jobPlace = elements.get(0);
            if (jobPlace != null) {
                vacancy.setLocation(jobPlace.text());
            }
        }
        elements = doc.select("div.card-body");
        //System.out.println(elements.toString());


        String hours = getHoursFromPage(elements);
        vacancy.setHours(hours);
    }

    /*
     *  Retrieve the hours respectively the minimum allowed hours from the relevant
     *  part of the page
     */
    private String getHoursFromPage(Elements elements) {
        try {
            // Search the childnodes for the tag "<strong>Uren per week:</strong>
            // in principle, the text is free format with a few common headings
            for (Element e: elements) {
                for (Element child: e.children()) {
                    //System.out.println(child.toString());
                    String minString = "<strong>Minimum aantal uren per week</strong>";
                    if (child.toString().contains("Uren per week")) {
                        String uren = child.text();
                        System.out.println(uren);
                        String[] urenArr = uren.split(":");
                        if (urenArr.length > 1) {
                            uren = urenArr[1];
                            return uren.trim();
                        }
                    } else if (child.toString().contains(minString)) {
                        String sElement = child.toString();
                        int index = child.toString().indexOf(minString);
                        index += minString.length();
                        String sRest = sElement.substring(index);
                        index = sRest.indexOf("<");
                        String sUren = sRest.substring(0, index);
                        return sUren;
                    }
                }
            }
        } catch (Exception e) {
            // nothing, it will not always parse.
            return "0";
        }
        return "0"; // catch all when working hours not mentioned on the page
    }

    @Override
    protected List<String> getVacancySpecifics(Document doc) {
        return null;
        // this method is simply not used (yet)
    }

    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {

    }

    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {

    }
}
