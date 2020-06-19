package nl.ordina.jobcrawler.scrapers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import nl.ordina.jobcrawler.model.Vacancy;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class HuxleyITVacancyScraperTest {

    @InjectMocks
    private HuxleyITVacancyScraper huxleyITVacancyScraper;

    @Mock
    private RestTemplate restTemplateMock;

    private static ResponseEntity<HuxleyITResponse> jsonResponse;
    private static ResponseEntity<HuxleyITResponse> noDataResponse;

    @BeforeClass
    public static void init() throws Exception {
        // Saved .json response in resources folder is being used in this test. Content of this file is needed.
        File jsonFile = getFile("/HuxleyIT/postRequestResponse.json");
        // We need to map the data from the jsonFile according to our HuxleyItResponse.class
        HuxleyITResponse huxleyITResponse = new ObjectMapper().readValue(jsonFile, HuxleyITResponse.class);
        jsonResponse = new ResponseEntity<>(huxleyITResponse, HttpStatus.OK);

        File jsonFileNoData = getFile("/HuxleyIT/postRequestNoData.json");
        HuxleyITResponse huxleyITResponseNoData = new ObjectMapper().readValue(jsonFileNoData, HuxleyITResponse.class);
        noDataResponse = new ResponseEntity<>(huxleyITResponseNoData, HttpStatus.OK);
    }

    @Test
    public void randomTest() {
        when(restTemplateMock.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(jsonResponse);

        // Call the function. We can enter any int we want as it should always return our own data.
        HuxleyITResponse huxleyITResponse = huxleyITVacancyScraper.scrapeVacancies(1);
        assertEquals(4, huxleyITResponse.getHits());
    }

    @Test
    public void scrapeVacancies_should_return_HuxleyITResponse() throws IOException {
        // When the restTemplate.getForEntity method is used, regardless of the URL and class, we return our own data.
        when(restTemplateMock.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(jsonResponse);

        // Call the function. We can enter any int we want as it should always return our own data.
        HuxleyITResponse huxleyITResponse = huxleyITVacancyScraper.scrapeVacancies(1);
        Map<String, Object> jsonVacancy = huxleyITResponse.getVacanciesData().get(0);

        // Assert that the return value matches the expected value.
        assertEquals(4, huxleyITResponse.getHits());
        assertEquals("Vacancy 1 description", jsonVacancy.get("description"));

        // Verify that restTemplate.getForEntity was called once.
        verify(restTemplateMock, times(1)).postForEntity(anyString(), any(), any(Class.class));
    }

    @Test(expected = MismatchedInputException.class)
    public void scrapeVacancies_should_not_return_a_valid_response_empty_json_file() throws IOException {
        // Set up data again for this test
        File jsonFile = getFile("/HuxleyIT/postRequestEmptyFile.json");
        HuxleyITResponse huxleyITResponse = new ObjectMapper().readValue(jsonFile, HuxleyITResponse.class);

        // Todo: Handle this possible exception
    }

    @Test
    public void scrapeVacancies_should_not_return_anything_empty_json_body() throws IOException {
        // When the restTemplate.getForEntity method is used, regardless of the URL and class, we return our own data.
        when(restTemplateMock.postForEntity(anyString(), any(), any(Class.class)))
                .thenReturn(noDataResponse);

        // Call the function. We can enter any int we want as it should always return our own data.
        HuxleyITResponse huxleyITResponse = huxleyITVacancyScraper.scrapeVacancies(1);

        // As the json body is empty we're not expecting any real results.
        assertNull(huxleyITResponse.getHits());

        // Todo: If the json body is empty we might get a nullpointerexception. Need to handle that in the scraper.
        assertNull(huxleyITResponse.getVacanciesData());

        // Verify that restTemplate.getForEntity was called once.
        verify(restTemplateMock, times(1)).postForEntity(anyString(), any(), any(Class.class));
    }

    @Test
    public void getVacancies_should_return_a_list_of_2_vacancies() throws IOException {
        // Spy the scraper as we call methods from its own class.
        HuxleyITVacancyScraper huxleySpy = spy(new HuxleyITVacancyScraper());

        // Setup return values for methods in same class.
        doReturn(jsonResponse.getBody()).when(huxleySpy).scrapeVacancies(anyInt());

        // Retrieve vacancies
        List<Vacancy> vacancies = huxleySpy.getVacancies();

        // Assert that list contains 2 vacancies
        assertEquals(4, vacancies.size());

        for (Vacancy vacancy : vacancies) {
            assertEquals("HuxleyIT", vacancy.getBroker());
            assertTrue(vacancy.getAbout().contains("description"));
            assertTrue(vacancy.getVacancyURL().contains("https://www.huxley.com/nl-nl/job/kyc/"));
        }

        verify(huxleySpy, times(2)).scrapeVacancies(anyInt());
    }

    @Test(expected = NullPointerException.class)
    public void getVacancies_should_throw_NullPointerException() {
        // Spy the scraper as we call methods from its own class.
        HuxleyITVacancyScraper huxleySpy = spy(new HuxleyITVacancyScraper());

        // Setup return values for methods in same class.
        doReturn(noDataResponse.getBody()).when(huxleySpy).scrapeVacancies(anyInt());

        // Retrieve vacancies
        // Throws NullPointerException as response.getPages() isn't available.
        // Todo: fix above issue
        List<Vacancy> vacancies = huxleySpy.getVacancies();
    }

    // This method is used to retrieve the file content for local saved html files.
    private static File getFile(String fileName) {
        try {
            URL fileContent = HuxleyITVacancyScraperTest.class.getResource(fileName);
            return fileContent != null ? new File(fileContent.toURI()) : new File("/404");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
