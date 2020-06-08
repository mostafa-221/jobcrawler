package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.SearchRequest;
import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SearchRequestController.class)
class SearchRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService vacancyService;

    private Vacancy mockVacancy;

    @BeforeEach
    void setUp() {
        mockVacancy = ControllerTest.vacancyFactory("vacancy");
    }

    /**
     * Tests the /searchrequest end point,
     * for now all it needs to do is to return a new SearchResult with all vacancies
     */
    @Test
    void searchRequest() throws Exception {
        Vacancy mockVacancy2 = ControllerTest.vacancyFactory("vacancy2");
        List<Vacancy> vacancies = Arrays.asList(mockVacancy, mockVacancy2);
        doReturn(vacancies).when(vacancyService).getAllVacancies();

        mockMvc.perform(post("/searchrequest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ControllerTest.asJsonString(new SearchRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string(ControllerTest.asJsonString(new SearchResult(new SearchRequest(), vacancies))));
    }
}