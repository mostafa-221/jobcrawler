package nl.ordina.jobcrawler.scrapers;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Vacancy;
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

    private static final int MAX_NR_OF_PAGES = 25;  // 25 seems enough for demo purposes, can be up to approx 60
    // at a certain point the vacancy date will be missing

    /**
     * Default constructor that calls the constructor from the abstract class.
     */
    public JobBirdScraper() {
        super(
                "https://www.jobbird.com/nl/vacature?s=java&rad=30&page=", // Required search URL. Can be retrieved using getSEARCH_URL()
                "Jobbird" // Required broker. Can be retrieved using getBROKER()
        );
    }

    /**
     * Default function to start scraping vacancies.
     *
     * @return List with vacancies.
     */
    @Override
    public List<Vacancy> getVacancies() {
        List<Vacancy> vacancies = new ArrayList<>();
        try {
            log.info(String.format("%s -- Start scraping", getBROKER().toUpperCase()));

            List<String> vacancyURLs = retrieveVacancyURLs();
            for (String vacancyURL : vacancyURLs) {
                Document doc = getDocument(vacancyURL);
                if (doc != null) {
                    Vacancy vacancy = Vacancy.builder()
                            .vacancyURL(vacancyURL)
                            .title(retrieveVacancyTitle(doc))
                            .hours(retrieveHoursFromPage(doc))
                            .broker(getBROKER())
                            .location(retrieveLocation(doc))
                            .postingDate(retrievePublishDate(doc))
                            .about(retrieveVacancyAbout(doc))
                            .build();

                    vacancies.add(vacancy);
                    log.info(String.format("%s - Vacancy found: %s", getBROKER(), vacancy.getTitle()));
                }
            }
            log.info(String.format("%s -- Returning scraped vacancies", getBROKER()));

        } catch (HTMLStructureException e) {
            log.error("HTML structure of jobbird changed in critical way: " + e.getLocalizedMessage());
        }
        return vacancies;
    }

    /**
     * Create seach url based on pageNumber.
     *
     * @param pageNumber number that's needed to create search url.
     * @return String, full search url for specific page.
     * @throws Exception when pageNumber is below 1.
     */
    private String createSearchURL(int pageNumber) throws Exception {
        if (pageNumber < 1) {
            throw new Exception("JobBirdScraper:createSearchURL: pagenr must be 1 or greater");
        }
        return String.format("%s%d&ot=date&c[]=ict", getSEARCH_URL(), pageNumber);
    }


    /**
     * Retrieve all vacancyURLs from JobBird.
     *
     * @return A list of Strings containing the urls to the vacancies.
     */
    protected List<String> retrieveVacancyURLs() throws HTMLStructureException {
        //  Returns a List with VacancyURLs
        ArrayList<String> vacancyURLs = new ArrayList<>();

        try {
            // on each page including this first one are vacancies to be found
            Document doc = getDocument(createSearchURL(1));

            boolean continueSearching = true;
            for (int i = 1; continueSearching && i <= getLastPageToScrape(doc); i++) {
                String searchURL = createSearchURL(i);
                doc = getDocument(searchURL);

                ArrayList<String> vacancyUrlsOnPage = retrieveVacancyURLsFromDoc(doc);

                continueSearching = continueSearching(vacancyURLs, vacancyUrlsOnPage);

                if (continueSearching) {
                    vacancyURLs.addAll(vacancyUrlsOnPage);
                }
            }

        } catch (Exception e) {
            throw new HTMLStructureException("retrieveVacancyURLs");
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
    private boolean continueSearching(ArrayList<String> vacancyURLs, ArrayList<String> vacancyUrlsOnPage) {
        for (String vacancyUrlOnPage : vacancyUrlsOnPage) {
            if (vacancyURLs.contains(vacancyUrlOnPage)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieve the index of the last page to scrape according to this page
     *
     * @param doc The HTML document containing the URLs to the vacancies
     * @return the index of the last page to scrape
     */
    private int getLastPageToScrape(Document doc) throws HTMLStructureException {
        int totalNumberOfPages = retrieveTotalNumberOfPages(doc);
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
    protected int retrieveTotalNumberOfPages(Document doc) throws HTMLStructureException {
        try {
            Elements elements = doc.select("span.page-link");
            Element parent = elements.first().parent().parent();
            Elements children = parent.children();
            int count = 0;
            for (Element child : children) {
                String text = child.text();
                if (!text.equalsIgnoreCase("volgende")) count++;
            }
            log.info(String.format("%s -- Total number of pages: %d", getBROKER(), count));
            return count;
        } catch (Exception e) {
            throw new HTMLStructureException("retrieveTotalNumberOfPages");
        }
    }

    /*
     *    Retrieve the links to the individual pages for each assignment
     */
    private ArrayList<String> retrieveVacancyURLsFromDoc(Document doc) throws HTMLStructureException {
        try {
            ArrayList<String> result = new ArrayList<>();
            Elements elements = doc.select("div.jobResults");
            Element element = elements.first();
            Element parent = element.parent();
            Elements lijst = parent.children();

            Elements httplinks = lijst.select("a[href]");
            for (Element e : httplinks) {
                String sLink = e.attr("abs:href");
                result.add(sLink);
            }

            return result;
        } catch (Exception e) {
            throw new HTMLStructureException("retrieveVacancyURLsFromDoc");
        }
    }

    /**
     * Retrieve the vacancy title
     * THIS IS VITAL. if not, scraping by JobBird should be interrupted, there is no point to
     * retrieve vacancies without a title
     *
     * @param doc Document which is needed to retrieve vacancy title
     * @return String vacancy title
     */
    protected String retrieveVacancyTitle(Document doc) throws HTMLStructureException {

        try {
            Element vacancyHeader = doc.select("h1.no-margin").first();

            if (vacancyHeader != null) {
                return vacancyHeader.text();
            } else {
                throw new HTMLStructureException("retrieveVacancyTitle");
            }
        } catch (Exception e) {
            throw new HTMLStructureException("retrieveVacancyTitle");
        }
    }

    /**
     * Retrieve location from vacancy
     *
     * @param doc Document which is needed to retrieve vacancy location
     * @return String vacancy location
     */
    protected String retrieveLocation(Document doc) {
        Elements elements = doc.select("span.job-result__place");
        if (!elements.isEmpty()) {
            Element jobPlace = elements.get(0);
            if (jobPlace != null) {
                return jobPlace.text();
            }
        }

        return "";
    }

    /**
     * Retrieve publishing date from vacancy
     *
     * TO DO vacancy date is also to be found in the header. In case the job result__place tag
     * is missing, it can still be retrieved there.
     *
     * @param doc Document which is needed to retrieve publishing date
     * @return String publish date
     */
    protected String retrievePublishDate(Document doc) {
        String result = "";
        Elements elements = doc.select("span.job-result__place");
        if (!elements.isEmpty()) {
            Element parent = elements.get(0).parent().parent();

            Elements timeElements = parent.select("time");
            log.debug(timeElements.toString());

            if (!timeElements.isEmpty()) {
                Element timeElement = timeElements.get(0);
                result = timeElement.attr("datetime");
            }
        }
        return result;
    }

    /**
     * Retrieve the hours respectively the minimum allowed hours frm the relevant part of the page.
     *
     * @param doc Document which is needed to retrieve hours
     * @return String hours
     */
    protected String retrieveHoursFromPage(Document doc) {
        try {
            Elements elements = doc.select("div.card-body");
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
                        String sUren = sRest.substring(0, index);
                        return sUren.trim();
                    }
                }
            }
        } catch (Exception e) {
            // nothing, it will not always parse.
            return "0";
        }
        return "0"; // catch all when working hours not mentioned on the page
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

    /**
     * Retrieve the vacancy body to store in postgres database
     *
     * @param doc Document which is needed to retrieve the body
     * @return String vacancy body
     */
    private String retrieveVacancyAbout(Document doc) {
        Elements aboutElements = doc.select("div.jobContainer");
        return aboutElements.text();
    }
}
