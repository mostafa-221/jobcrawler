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
import java.util.Map;

/*  Search is limited to URLs for ICT jobs with search term "java"
 *       Search URL will be completed later:a page number is added to the url
 *       example: page = 5
 *       https://www.jobbird.com/nl/vacature?s=java&rad=30&page=5&ot=date&c[]=ict
 *
 *       on each page including the first, links to vacancies can be found
 *       stop criterium: either the maximum number of pages (MAXNRPAGES) has been processed or
 *       the criterium "no more pages"
 *
 *       no more pages: when a page is retrieved for a page number higher than the
 *       highest page number available for the search, the last page and thus the last
 *       set of urls is returned - we can check whether one of those links is already in the set.
 *
 *       for demo purposes, the max nr of pages can be about 20, this suffices
 *       to max it out, it could be approximately 60, after a certain number of pages
 *       the vacancy date will be missing
 *
 *       In order to be able to check whether the program is still running, the vacancies are logged
 *       (log.info()). You may want to change this to log.debug().
 *
 */

@Slf4j
@Component
public class JobBirdScraper extends VacancyScraper {



    private static final String SEARCH_URL =  "https://www.jobbird.com/nl/vacature?s=java&rad=30&page=";
        // after the page number, add "&ot=date&c[]=ict";

    private static final String BROKER = "Jobbird";

    private static final int MAXNRPAGES = 25;  // 25 seems enough for demo purposes, can be up to approx 60
                                               // at a certain point the vacancy date will be missing

    public JobBirdScraper(ConnectionDocumentService connectionDocumentService) {
        super(connectionDocumentService, SEARCH_URL, BROKER);
    }


    private String createSearchURL(int aPagenr) throws Exception {
        if (aPagenr >=1) {
            return SEARCH_URL + aPagenr + "&ot=date&c[]=ict";
        } else {
            throw new Exception("JobBirdScraper:createSearchURL: pagenr must be 1 or greater");
        }
    }

    @Override
    protected List<VacancyURLs> getVacancyURLs() throws IOException {
        //  Returns a List with VacancyURLs
        ArrayList<VacancyURLs> URLs = new ArrayList<>();


        try {
            boolean continueSearching = true;
            int i = 1;
            while ((continueSearching) && (i <= MAXNRPAGES)) {
                String URL = createSearchURL(i++);
                Document doc = getDocument(URL);
                ArrayList<VacancyURLs> a = retrieveVacancyURLsFromDoc(doc);
                for (int j = 0; j <= a.size()-1; j++) {
                    if (URLs.contains(a.get(j))) {
                        continueSearching = false;
                        break;
                    }
                }
                if (continueSearching)
                    URLs.addAll(a);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return URLs;
    }

    /* Not used - the number of pages in the block under the page is steadily extended
     * each time the next URL is generated and the corresponding page is fetched.
     * Instead, a maximum is used for the number of pages accessed.
     *
     * Find the total number of pages, this can be found via the maneuver block at the bottom of the page
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
            if (!text.equalsIgnoreCase("volgende")) count++;
        }
        log.info("jobbird: total number of pages is " + count);
        return count;
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
            log.debug(sLink);
            result.add(new VacancyURLs(sLink));
        }

        return result;
    }


    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) {

        Element vacancyHeader = doc.select("h1.no-margin").first();

        if(vacancyHeader != null) {
            String title = vacancyHeader.text();
            vacancy.setTitle(title);
            log.info("vacancy found: "  + title);
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
    *   A number of vacancy page are in Dutch and quite often, the "About" can be found between
    *   a line (div) "Functieomschrijving" just after  <div id="jobContent"  class = "card-body>
    *  and a heading <h3>Vaardigheden</h3>
    *
    *   A number of vacancy pages have the about section
    *
    *  we cannot be sure about the exact layout, so it would be possible to extract the portion just
    *  after the jobContent when the first line contains Functieomschrijving, read until Vaardigheden.
    *
    *
    *  In other cases it is not so simple. When aforementioned receipt does not work we can
    *  we can do the following:
    *
    * gather all elements until one of the following occurs:
    *  - english offer: A sentence containing skills
    *
    *
    * For the time being, the About contains all text contained within the jobcontainer card div element
    *   <div class="jobContainer card">
    *
    * */
    @Override
    protected void setVacancyAbout(Document doc, Vacancy vacancy) {
        Elements abou1tElements = doc.select("div.jobContainer");
        vacancy.setAbout(abou1tElements.text());
    }

    @Override
    protected void setVacancySkillSet(Document doc, Vacancy vacancy) {

    }
}
