package nl.ordina.jobcrawler.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class YachtVacancyScraperTest {

    @InjectMocks
    private YachtVacancyScraper yachtVacancyScraper;

    @Mock
    private RestTemplate restTemplateMock;

    @Mock
    private Document documentMock;

    private static Document vacancyDoc;
    private static Document removedVacancyDoc;

    private static ResponseEntity<YachtVacancyResponse> jsonResponse;
    private static ResponseEntity<YachtVacancyResponse> noDataResponse;

    @BeforeClass
    public static void init() throws Exception {
        // Grab the file content of the files mentioned below. Parse this file using jsoup as HTML.

        File vacancyDocHtml = getFile("/yacht/yachtvacancy.html");
        vacancyDoc = Jsoup.parse(vacancyDocHtml, "UTF-8");

        File removedVacancyHtml = getFile("/yacht/yachtremovedvacancy.html");
        removedVacancyDoc = Jsoup.parse(removedVacancyHtml, "UTF-8");

        // Saved .json response in resources folder is being used in this test. Content of this file is needed.
        File jsonFile = getFile("/yacht/getRequestResponse.json");
        // We need to map the data from the jsonFile according to our YachtVacancyResponse.class
        YachtVacancyResponse yachtVacancyResponse = new ObjectMapper().readValue(jsonFile, YachtVacancyResponse.class);
        jsonResponse = new ResponseEntity<>(yachtVacancyResponse, HttpStatus.OK);

        File jsonFileNoData = getFile("/yacht/getRequestResponseNoData.json");
        YachtVacancyResponse yachtVacancyResponseNoData = new ObjectMapper().readValue(jsonFileNoData, YachtVacancyResponse.class);
        noDataResponse = new ResponseEntity<>(yachtVacancyResponseNoData, HttpStatus.OK);
    }

    @Test
    public void scrapeVacancies_should_return_YachtVacancyResponse() throws IOException {
        // When the restTemplate.getForEntity method is used, regardless of the URL and class, we return our own data.
        when(restTemplateMock.getForEntity(anyString(), any(Class.class)))
                .thenReturn(jsonResponse);

        // Call the function. We can enter any int we want as it should always return our own data.
        YachtVacancyResponse yachtResponse = yachtVacancyScraper.scrapeVacancies(1);

        // Assert that the return value matches the expected values.
        assertEquals(1, yachtResponse.getCurrentPage());
        assertEquals(1, yachtResponse.getPages());
        assertEquals(2, yachtResponse.getVacancies().size());

        // Verify that restTemplate.getForEntity was called once.
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(Class.class));
    }

    @Test(expected = MismatchedInputException.class)
    public void scrapeVacancies_should_not_return_a_valid_response_empty_json_file() throws IOException {
        // Set up data again for this test
        File jsonFile = getFile("/yacht/getRequestResponseEmptyFile.json");
        YachtVacancyResponse yachtVacancyResponse = new ObjectMapper().readValue(jsonFile, YachtVacancyResponse.class);

        // Todo: Handle this possible exception in YachtVacancyScraper.java
    }

    @Test
    public void scrapeVacancies_should_not_return_anything_empty_json_body() throws IOException {
        // When the restTemplate.getForEntity method is used, regardless of the URL and class, we return our own data.
        when(restTemplateMock.getForEntity(anyString(), any(Class.class)))
                .thenReturn(noDataResponse);

        // Call the function. We can enter any int we want as it should always return our own data.
        YachtVacancyResponse yachtResponse = yachtVacancyScraper.scrapeVacancies(1);

        // As the json body is empty we're not expecting any real results.
        assertNull(yachtResponse.getCurrentPage());
        assertNull(yachtResponse.getPages());

        // Todo: If the json body is empty we might get a nullpointerexception. Need to handle that in the scraper.
        assertNull(yachtResponse.getVacancies());

        // Verify that restTemplate.getForEntity was called once.
        verify(restTemplateMock, times(1)).getForEntity(anyString(), any(Class.class));
    }

    @Test
    public void getVacancyAbout_should_return_vacancy_body() {
        // We call the real function and pass our yachtvacancy.html document to this method.
        String vacancyAbout = yachtVacancyScraper.getVacancyAbout(vacancyDoc);

        // Assert that the returned string contains what is expected.
        assertEquals("Random mock data", vacancyAbout);
    }

    @Test(expected = NullPointerException.class)
    public void getVacancyAbout_should_return_an_empty_string() {
        String vacancyAbout = yachtVacancyScraper.getVacancyAbout(removedVacancyDoc);

        // Todo: YachtVacancyScraper currently not handles a document where it can not select the cssQuery.
        // Throws a NullPointerException for now.
    }

    @Test(expected = NullPointerException.class)
    public void getVacancyAbout_should_throw_exception() {
        when(documentMock.select(anyString())).thenReturn(new Elements());

        // Line below causes a nullpointerexception as it can't select anything on an empty document.
        String vacancyAboutDocMock = yachtVacancyScraper.getVacancyAbout(documentMock);

        // Todo: handle nullpointerexception
    }

    @Test
    public void getSkills_should_return_valid_skillSet() {
        // Pass our vacancyDocument to the method to retrieve the skillset.
        Set<Skill> skillSet = yachtVacancyScraper.getSkills(vacancyDoc);

        // Document contains 4 skills.
        assertEquals(4, skillSet.size());
    }

    @Test
    public void getSkills_should_return_empty_skillSet() {
        // Passing the removedVacancyDoc to the yachtVacancyScraper.getSkills method
        Set<Skill> skillSet = yachtVacancyScraper.getSkills(removedVacancyDoc);

        // Expect empty skillSet
        assertEquals(0, skillSet.size());
    }

    @Test
    public void getSkills_should_return_empty_skillSet_DocumentMock() {
        // Pass the documentMock to the getSkills method.
        when(documentMock.select(anyString())).thenReturn(new Elements());
        Set<Skill> skillSet = yachtVacancyScraper.getSkills(documentMock);

        // Assert that the skillSet is empty.
        assertTrue(skillSet.isEmpty());

        // Verify that the select function was used once on the documentMock.
        verify(documentMock, times(1)).select(anyString());
    }

    @Test
    public void getVacancies_should_return_a_list_of_2_vacancies() throws IOException {
        // Spy the scraper as we call methods from its own class.
        YachtVacancyScraper yachtSpy = spy(new YachtVacancyScraper());

        // Setup return values for methods in same class.
        doReturn(jsonResponse.getBody()).when(yachtSpy).scrapeVacancies(anyInt());
        doReturn(vacancyDoc).when(yachtSpy).getYachtDocument(anyString());

        // Retrieve vacancies
        List<Vacancy> vacancies = yachtSpy.getVacancies();

        // Assert that list contains 2 vacancies
        assertEquals(2, vacancies.size());

        for (Vacancy vacancy : vacancies) {
            assertEquals("Yacht", vacancy.getBroker());
            assertEquals("Random mock data", vacancy.getAbout());
            assertEquals(4, vacancy.getSkills().size());
            assertTrue(vacancy.getTitle().contains("Vacancy"));
            assertTrue(vacancy.getVacancyURL().contains("https://www.yacht.nl/vacatures/"));
        }

        verify(yachtSpy, times(1)).scrapeVacancies(anyInt());
        verify(yachtSpy, times(2)).getYachtDocument(anyString());
    }

    @Test(expected = NullPointerException.class)
    public void getVacancies_should_throw_NullPointerException() throws IOException {
        // Spy the scraper as we call methods from its own class.
        YachtVacancyScraper yachtSpy = spy(new YachtVacancyScraper());

        // Setup return values for methods in same class.
        doReturn(noDataResponse.getBody()).when(yachtSpy).scrapeVacancies(anyInt());

        // Retrieve vacancies
        // Throws NullPointerException as response.getPages() isn't available.
        // Todo: fix above issue
        List<Vacancy> vacancies = yachtSpy.getVacancies();
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
