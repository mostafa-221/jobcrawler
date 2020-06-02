package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

    /*  Search URL will be completed later: either a page is added to the URL or not.

        First page:

    private static final String SEARCH_URL =  "https://www.jobbird.com/nl/vacature?s=java&rad=30&page=";
        // after the page number, add "&ot=date&c[]=ict";

        Later pages, for example page 3:

        SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java&page=3&rad=30&ot=date";
     */
    private static final String SEARCH_URL = "https://www.jobbird.com/nl/vacature?s=java";
    private static final String BROKER = "Jobbird";

    private static final int MAX_NR_OF_PAGES = 25;  // 25 seems enough for demo purposes, can be up to approx 60
    // at a certain point the vacancy date will be missing

    public JobBirdScraper() {
        super(SEARCH_URL, BROKER);
    }


    private String createSearchURL(int pageNumber) throws Exception {
        if (pageNumber < 1) {
            throw new Exception("JobBirdScraper:createSearchURL: pagenr must be 1 or greater");
        }
        return SEARCH_URL + pageNumber + "&ot=date&c[]=ict";
    }

    @Override
    protected List<VacancyURLs> getVacancyURLs() {
        //  Returns a List with VacancyURLs
        ArrayList<VacancyURLs> vacancyURLs = new ArrayList<>();

        try {
            // on each page including this first one are vacancies to be found
            Document doc = getDocument(createSearchURL(1));

            boolean continueSearching = true;
            for (int i = 1; continueSearching && i <= getLastPageToScrape(doc); i++) {
                String searchURL = createSearchURL(i);
                doc = getDocument(searchURL);

                ArrayList<VacancyURLs> vacancyUrlsOnPage = retrieveVacancyURLsFromDoc(doc);

                continueSearching = continueSearching(vacancyURLs, vacancyUrlsOnPage);

                if (continueSearching) {
                    vacancyURLs.addAll(vacancyUrlsOnPage);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return vacancyURLs;
    }

    /**
     * Continue searching if this page only contains new vacancies. If any of the vacancies is already know, stop searching.
     *
     * @param vacancyURLs       known vacancyURLs for this scraping session
     * @param vacancyUrlsOnPage VanacyURLS on the current page
     * @return true if none of the vacancies on this page has been encountered before in this scraping session
     */
    private boolean continueSearching(ArrayList<VacancyURLs> vacancyURLs, ArrayList<VacancyURLs> vacancyUrlsOnPage) {
        for (VacancyURLs vacancyUrlOnPage : vacancyUrlsOnPage) {
            if (vacancyURLs.contains(vacancyUrlOnPage)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the index of the last page to scrape
     *
     * @param doc The HTML document containing the URLs to the vacancies
     * @return the index of the last page to scrape
     */
    private int getLastPageToScrape(Document doc) {
        int totalNumberOfPages = getTotalNumberOfPages(doc);
        // TODO: we could get more sophisticated logic in place to limit the number of pages.
        // For example, we could look at the posting date of each vacancy, and limit it to thirty days.
        return Math.min(totalNumberOfPages, MAX_NR_OF_PAGES);

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
        try {
            Elements elements = doc.select("span.page-link");
            Element parent = elements.first().parent().parent();
            Elements children = parent.children();
            int count = 0;
            for (Element child : children) {
                String text = child.text();
                if (!text.equalsIgnoreCase("volgende")) count++;
            }
            log.info("jobbird: total number of pages is " + count);
            return count;
        } catch (Exception e) {
            log.error("getTotalNumberOfPages: unexpected page structure");
            return 0;
        }
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
        for (Element e : httplinks) {
            String sLink = e.attr("abs:href");
            log.debug(sLink);
            result.add(new VacancyURLs(sLink));
        }

        return result;
    }


    @Override
    protected void setVacancyTitle(Document doc, Vacancy vacancy) throws HTMLStructureException {

        Element vacancyHeader = doc.select("h1.no-margin").first();

        if (vacancyHeader != null) {
            String title = vacancyHeader.text();
            vacancy.setTitle(title);
            log.info("vacancy found: " + title);
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

        if (!elements.isEmpty()) {
            String hours = getHoursFromPage(elements);
            vacancy.setHours(hours);
        }

        elements = doc.select("span.job-result__place");
        if (!elements.isEmpty()) {
            String publishDate = getPublishDate(elements);
            vacancy.setPostingDate(publishDate);
        }
    }

    private String getPublishDate(Elements elements) {
        String result = "";

        try { // may not always parse
                Element parent = elements.get(0).parent().parent();

                Elements timeElements = parent.select("time");
                log.debug(timeElements.toString());

                if (!timeElements.isEmpty()) {
                    Element timeElement = timeElements.get(0);
                    result = timeElement.attr("datetime");
            }
        } catch (Exception e) {
            // nothing, it may not always parse
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
            for (Element e : elements) {
                for (Element child : e.children()) {
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
                        String sUren = sRest.substring(0, index).trim();
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
