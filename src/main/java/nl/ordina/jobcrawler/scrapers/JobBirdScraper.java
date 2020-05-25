package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
public class JobBirdScraper extends VacancyScraper {

    /*  Search URL will be completed later: either a page is added to the URL or not.

        First page:

        SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java&rad=30&ot=date";

        Later pages, for example page 3:

        SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java&page=3&rad=30&ot=date";
     */
    private static final String SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java";
    private static final String BROKER = "Jobbird";

    public JobBirdScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }


    private String createSearchURL(int aPagenr) throws Exception {
        if (aPagenr == 1) {
            return SEARCH_URL + "&rad=30&ot=date";
        } else
        if (aPagenr > 1) {
            return SEARCH_URL + "&rad=30&page=" + aPagenr + "&ot=date";
        } else {
            throw new Exception("JobBirdScraper:createSearchURL: pagenr must be 1 or greater");
        }
    }

    @Override
    public List<VacancyURLs> getVacancyURLs() throws IOException {
        //  Returns a List with VacancyURLs
        ArrayList<VacancyURLs> URLs = new ArrayList<>();

        try {
            // on each page including this first one are vacancies to be found
            Document doc = getDocument(createSearchURL(1));
            int totalNrOfPages = getTotalNumberOfPages(doc);
            for (int i=1; i<=totalNrOfPages; i++) {
                if (i>1) {
                    doc = getDocument(createSearchURL(i));
                }
                URLs.addAll(retrieveVacancyURLsFromDoc(doc));
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return URLs;
    }


    /*
    *    Retrieve the links to the individual pages for each assignment
    */
    protected ArrayList<VacancyURLs> retrieveVacancyURLsFromDoc(Document doc) {
        ArrayList<VacancyURLs> result = new ArrayList<>();
        Elements elements = doc.select("div.jobResults");
        Element element = elements.first();
        Element parent = element.parent();
        Elements lijst = parent.children();

        Elements linklijst = new Elements();
        Elements httplinks = lijst.select("a[href]");
        for (Element e: httplinks) {
            String sLink = e.attr("abs:href");
            log.debug(sLink);
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

        Elements children;
        // if elements not found or structure not as expected, return 0
        try {
            Element parent = elements.first();
            parent = parent.parent();
            parent = parent.parent();
            children = parent.children();
        } catch (Exception e) {
            log.error("getTotalNumberOfPages: unexpected page structure");
            return 0;
        }


        int count = 0;
        for (Element child: children) {
            String text = child.text();
            if (!text.equalsIgnoreCase("volgende")) count++;
        }
        log.info("jobbird: total number of pages is " + count);
        return count;
    }

    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) throws HTMLStructureException {

        Element vacancyHeader = doc.select("h1.no-margin").first();

        if(vacancyHeader != null) {
            String title = vacancyHeader.text();
            vacancy.setTitle(title);
            log.info("vacancy found: "  + title);
        } else {
            throw new HTMLStructureException("title missing");
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

        String hours = getHoursFromPage(elements);
        vacancy.setHours(hours);

        String publishDate = getPublishDate(doc);
        vacancy.setPostingDate(publishDate);
    }

    private String getPublishDate(Document doc) {
        String result = "";
        Elements elements = doc.select("span.job-result__place");
        if (!elements.isEmpty()) {
            Element parent = elements.get(0).parent().parent();

            Elements timeElements = parent.select("time");
            log.debug( timeElements.toString());

            if (!timeElements.isEmpty()) {
                Element timeElement = timeElements.get(0);
                result = timeElement.attr("datetime");
            }
        }
        return result;
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
                    String minString = "<strong>Minimum aantal uren per week</strong>";
                    if (child.toString().contains("Uren per week")) {
                        String uren = child.text();
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


    /*
    *   The job bird vacancy page structure is quite loose.
    * For the time being, the About contains all text contained within the jobcontainer card div element
    *   <div class="jobContainer card">
    *
    * It is vital that the about section contains all relevant information. If this jobcontainer card div element
    * is not found, the page structure has been altered in a way that the jobbird scraper can no longer
    * do any useful work - in this case, it is better to generate an exception.
    *
    * */
    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) throws HTMLStructureException {
        try {
            Elements abou1tElements = doc.select("div.jobContainer");
            vacancy.setAbout(abou1tElements.text());
        } catch (Exception e) { // page structure altered, no longer purposeful work for jobbird scraper
            throw new HTMLStructureException("jobbird about section altered, skipping jobbird vacancy");
        }
    }

    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {

    }
}
