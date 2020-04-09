package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.VacancyURLs;
import nl.ordina.jobcrawler.service.ConnectionDocumentService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class YachtVacancyScraperTest {

    // Mocking ConnectionDocumentService as I only need this to call the YachtVacancyScraper constructor
    private final ConnectionDocumentService connectionDocumentService = mock(ConnectionDocumentService.class);
    // Want to use the methods defined in the YachtVacancyScraper with a document that's included in the resources/yacht directory.
    private final YachtVacancyScraper yachtVacancyScraper = new YachtVacancyScraper(connectionDocumentService);
    // Need to mock YachtVacancyScraper to not let it search on the real SEARCH_URL but on the included .html file. Otherwise test might fail soon as site continuously changes.
    private final YachtVacancyScraper yachtVacancyScraperMock = mock(YachtVacancyScraper.class);
    private Document overviewDoc;
    private Document vacancyDoc;

    @Before
    public void init() throws IOException {
        // Grab the file content of the file mentioned below. Parse this file using jsoup as HTML.
        File ovDoc = getFile("/yacht/yachtvacancyoverview.html");
        overviewDoc = Jsoup.parse(ovDoc, "UTF-8", yachtVacancyScraper.getSEARCH_URL());

        List<VacancyURLs> vacancyURLs = new ArrayList<>();
        VacancyURLs vacancyURL = VacancyURLs.builder()
                .url("https://www.yacht.nl/vacatures/9080044/senior-iam-portal-developer")
                .hours("40")
                .build();
        vacancyURLs.add(vacancyURL);
        when(yachtVacancyScraperMock.getVacancyURLs()).thenReturn(vacancyURLs);
    }


    @Test
    public void getVacancyURLs_Test() throws IOException {
        List<VacancyURLs> vacancyURLs = yachtVacancyScraperMock.getVacancyURLs();
        assertEquals(1, vacancyURLs.size());
    }

    @Test
    public void getTotalNumberOfPages_Test() {
        Integer numberOfPages = yachtVacancyScraper.getTotalNumberOfPages(overviewDoc);
        assertEquals(32, numberOfPages);
    }

    public File getFile(String file) {
        try {
            URL fileName = YachtVacancyScraperTest.class.getResource(file);
            return fileName != null ? new File(fileName.toURI()) : new File("/404");
        } catch(URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

}
