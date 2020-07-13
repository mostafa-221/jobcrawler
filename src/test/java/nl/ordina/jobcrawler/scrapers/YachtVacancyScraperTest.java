package nl.ordina.jobcrawler.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
//@RunWith(PowerMockRunner.class)
@PrepareForTest(Jsoup.class)
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
//        Connection connection = Mockito.mock(Connection.class);
//        Mockito.when(connection.execute()).thenThrow(new IOException("test"));
//        PowerMockito.mockStatic(Jsoup.class);
//        PowerMockito.when(Jsoup.connect(Mockito.anyString())).
//                thenReturn(connection);
        when(restTemplateMock.getForEntity(anyString(), any(Class.class)))
               .thenReturn(jsonResponse);
        List<Vacancy> vacancyList = yachtVacancyScraper.getVacancies();
        Assert.assertEquals(2,vacancyList.size());
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
