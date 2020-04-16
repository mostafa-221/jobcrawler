package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Issues running into creating this test class:
 * The Yacht website might get an update in the future, causing css elements to change. That can lead to the malfunction of the scraper class.
 * For now I added two .html files into the resources/yacht directory of which one is the overview of vacancies. The other is an actual vacancy on which I can test the methods.
 * If the website does get changed, these test should still succeed due to the included .html files. Not sure if that's the way to go.
 */

public class YachtVacancyScraperTest {

    // Mocking ConnectionDocumentService as I only need this to call the YachtVacancyScraper constructor
    private final ConnectionDocumentService connectionDocumentService = mock(ConnectionDocumentService.class);
    // Want to use the methods defined in the YachtVacancyScraper with a document that's included in the resources/yacht directory.
    private final YachtVacancyScraper yachtVacancyScraper = new YachtVacancyScraper(connectionDocumentService);
    // Need to mock YachtVacancyScraper to not let it search on the real SEARCH_URL but on the included .html file. Otherwise test might fail soon as site can change anytime.
    private final YachtVacancyScraper yachtVacancyScraperMock = mock(YachtVacancyScraper.class);
    private static Vacancy vacancy;

    private static Document overviewDoc;
    private static Document vacancyDoc;

    // Nu.nl as other non-Yacht site document
    private static Document nuDoc;

    @BeforeClass
    public static void init() throws IOException {
        // Init nuDoc with nu.nl
        nuDoc = Jsoup.connect("https://nu.nl").get();
        // Grab the file content of the files mentioned below. Parse this file using jsoup as HTML.
        File overviewDocHtml = getFile("/yacht/yachtvacancyoverview.html");
        overviewDoc = Jsoup.parse(overviewDocHtml, "UTF-8");

        File vacancyDocHtml = getFile("/yacht/yachtvacancy.html");
        vacancyDoc = Jsoup.parse(vacancyDocHtml, "UTF-8");

        vacancy = Vacancy.builder()
                .skills(new HashSet<>())
                .build();
    }


    @Test
    public void getVacancyURLs_Test_Mock() throws IOException {
        List<VacancyURLs> vacancyURLsMock = new ArrayList<>();
        VacancyURLs vacancyURL = VacancyURLs.builder()
                .url("https://www.yacht.nl/vacatures/9080044/senior-iam-portal-developer")
                .hours("40")
                .build();
        vacancyURLsMock.add(vacancyURL);
        when(yachtVacancyScraperMock.getVacancyURLs()).thenReturn(vacancyURLsMock);

        final List<VacancyURLs> vacancyURLs = yachtVacancyScraperMock.getVacancyURLs();
        assertEquals(1, vacancyURLs.size());
    }

    @Test
    public void getVacancyURLs_Test() {
        final Elements vacancyLinkElements = overviewDoc.select("div.results article h2 a[href]");
        assertEquals(15, vacancyLinkElements.size());
    }

    @Test
    public void getVacancyURLs_NonYachtSite_Test() throws IOException {
        final Elements vacancyLinkElements = nuDoc.select("div.results article h2 a[href]");
        assertEquals(0, vacancyLinkElements.size());
    }

    @Test(expected = HttpStatusException.class)
    public void getVacancy_RemovedVacancy_Test() throws IOException {
        /*
         * Defined url below is a vacancy that does not exist anymore on Yacht.
         * Does not throw status 404, so we need to throw a 404 ourselves. Might be doable by searching for the word 'sorry'
         */
        final String url = "https://www.yacht.nl/vacatures/9079222/data-analist---modeler";
        final Document removedVacancyDoc = Jsoup.connect(url).get();
        if (removedVacancyDoc.text().toLowerCase().contains("sorry"))
            throw new HttpStatusException("Error 404 - Not found", 404, url);
    }

    @Test
    public void getTotalNumberOfPages_Test() {
        final Integer numberOfPages = yachtVacancyScraper.getTotalNumberOfPages(overviewDoc);
        assertEquals(32, numberOfPages);
    }

    @Test
    public void getTotalNumberOfPages_NonYachtSite_Test() {
        final Integer numberOfPages = yachtVacancyScraper.getTotalNumberOfPages(nuDoc);
        // getTotalNumberOfPages returns 1 when page number system is not found in document. Regardless if it's a valid vacancy site or not. We pass the current document to the method.
        assertEquals(1, numberOfPages);
    }

    @Test
    public void vacancyTitle_Test() {
        final Elements vacancyHeader = vacancyDoc.select("header.cf h1");
        assertEquals(1, vacancyHeader.size());
        assertEquals("Senior IAM Portal Developer", vacancyHeader.first().text());
        assertTrue(vacancyHeader.first().is("h1"));
    }

    @Test
    public void getVacancySpecifics_Test() {
        final List<String> vacancySpecifics = yachtVacancyScraper.getVacancySpecifics(vacancyDoc);
        assertTrue(vacancySpecifics.size() > 0);
        assertFalse(vacancySpecifics.get(0).isEmpty());
        assertTrue(vacancySpecifics.get(vacancySpecifics.size() - 1).contains("publicatiedatum"));
    }

    @Test
    public void getVacancySpecifics_NonYachtSite_Test() {
        final List<String> vacancySpecifics = yachtVacancyScraper.getVacancySpecifics(nuDoc);
        assertEquals(0, vacancySpecifics.size());
    }

    @Test
    public void vacancyAbout_Test() {
        yachtVacancyScraper.setVacancyAbout(vacancyDoc, vacancy);
        final String vacancyAbout = vacancy.getAbout();
        assertFalse(vacancyAbout.isEmpty());
    }

    @Test
    public void vacancyAbout_NonYachtSite_Test() {
        yachtVacancyScraper.setVacancyAbout(nuDoc, vacancy);
        final String vacancyAbout = vacancy.getAbout();
        assertTrue(vacancyAbout.isEmpty());
    }

    @Test
    public void vacancySkillSet_Test() {
        yachtVacancyScraper.setVacancySkillSet(vacancyDoc, vacancy);
        final Set<Skill> vacancySkillSet = vacancy.getSkills();
        assertEquals(12, vacancySkillSet.size());
    }

    @Test
    public void vacancySkillSet_NonYachtSite_Test() {
        yachtVacancyScraper.setVacancySkillSet(nuDoc, vacancy);
        final Set<Skill> vacancySkillSet = vacancy.getSkills();
        assertEquals(0, vacancySkillSet.size());
    }


    private static File getFile(String fileName) {
        try {
            URL fileContent = YachtVacancyScraperTest.class.getResource(fileName);
            return fileContent != null ? new File(fileContent.toURI()) : new File("/404");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

}
