package nl.ordina.jobcrawler.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class YachtVacancyScraperTest {

    @InjectMocks
    private YachtVacancyScraper yachtVacancyScraper;

    @Mock
    private VacancyScraper vacancyScraperMock;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private Document documentMock;

    @Mock
    private VacancyScraper vacancyScraper;

    private static Document VACANCY_DOC;
    private static Document REMOVED_VACANCY_DOC;

    private static ResponseEntity<YachtVacancyResponse> jsonResponse;
    private static ResponseEntity<YachtVacancyResponse> noDataResponse;

    @BeforeAll
    public static void init() throws Exception {
        // Grab the file content of the files mentioned below. Parse this file using jsoup as HTML.

        File vacancyDocHtml = getFile("/Yacht/yachtvacancy.html");
        VACANCY_DOC = Jsoup.parse(vacancyDocHtml, "UTF-8");

        File removedVacancyHtml = getFile("/Yacht/yachtremovedvacancy.html");
        REMOVED_VACANCY_DOC = Jsoup.parse(removedVacancyHtml, "UTF-8");

        // Saved .json response in resources folder is being used in this test. Content of this file is needed.
        File jsonFile = getFile("/Yacht/getRequestResponse.json");
        // We need to map the data from the jsonFile according to our YachtVacancyResponse.class
        YachtVacancyResponse yachtVacancyResponse = new ObjectMapper().readValue(jsonFile, YachtVacancyResponse.class);
        jsonResponse = new ResponseEntity<>(yachtVacancyResponse, HttpStatus.OK);

        File jsonFileNoData = getFile("/Yacht/getRequestResponseNoData.json");
        YachtVacancyResponse yachtVacancyResponseNoData = new ObjectMapper().readValue(jsonFileNoData, YachtVacancyResponse.class);
        noDataResponse = new ResponseEntity<>(yachtVacancyResponseNoData, HttpStatus.OK);
    }

    @Test
    public void test_getVacancies() throws IOException {
        when(restTemplateMock.getForEntity(anyString(), any(Class.class)))
                .thenReturn(jsonResponse);
//        when(Jsoup.connect(any()).userAgent(anyString()).get())
//                .thenReturn(VACANCY_DOC);
        when(vacancyScraperMock.getDocument(anyString()))
                .thenReturn(VACANCY_DOC);
        List<Vacancy> vacancyList = yachtVacancyScraper.getVacancies();
    }

    // This method is used to retrieve the file content for local saved html files.
    private static File getFile(String fileName) {
        try {
            URL fileContent = YachtVacancyScraperTest.class.getResource(fileName);
            return fileContent != null ? new File(fileContent.toURI()) : new File("/404");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
