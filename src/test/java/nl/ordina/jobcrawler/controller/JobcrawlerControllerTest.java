package nl.ordina.jobcrawler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.SkillService;
import nl.ordina.jobcrawler.service.VacancyService;
import nl.ordina.jobcrawler.service.VacancyStarter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest
class JobcrawlerControllerTest {

    @MockBean
    private VacancyService vacancyService;

    @MockBean
    private SkillService skillService;

    @MockBean
    private VacancyStarter vacancyStarter;

    @Autowired
    private MockMvc mockMvc;

    private Vacancy mockVacancy;
    private Skill mockSkill;

    private static Vacancy vacancyFactory(final String title){
        return Vacancy.builder()
                .id(UUID.randomUUID())
                .vacancyURL("example.com")
                .title(title)
                .broker("broker")
                .vacancyNumber("1")
                .hours("30")
                .location("location example")
                .postingDate("14 April 2020")
                .about("this is a description of the example job")
                .skills(new HashSet<Skill>())
                .build();
    }

    private static Skill skillFactory(final String title){
        return Skill.builder()
                .id(UUID.randomUUID())
                .name(title)
                .vacancies(new HashSet<Vacancy>())
                .build();
    }

    @BeforeEach
    void setUp() {
        mockVacancy = vacancyFactory("job1");
        mockSkill = skillFactory("skill1");

        mockVacancy.addSkill(mockSkill);
    }




    @Nested
    class VacancyControllerTest{
        @Test
        void addJob() throws Exception {
//            doReturn(mockVacancy).when(vacancyService).add(mockVacancy); // does not return the vacancy, not sure why
            doReturn(mockVacancy).when(vacancyService).add(any(Vacancy.class));

            mockMvc.perform(post("/addJobWithJson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockVacancy)))
                    .andExpect(status().isOk())
//                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(mockVacancy)));

        }

        @Test
        void getByIDFound() throws Exception {

            doReturn(Optional.of(mockVacancy)).when(vacancyService).getVacancyByID(mockVacancy.getId());

            ResultActions result = mockMvc.perform(get("/getByID/{id}", mockVacancy.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    .andExpect(content().string(asJsonString(mockVacancy)));

        }

        @Test
        void getByIDNotFound() throws Exception {

            doReturn(Optional.empty()).when(vacancyService).getVacancyByID(any(UUID.class));

            mockMvc.perform(get("/getByID/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());

        }

        @Test
        void getJobsByBroker() throws Exception {
            doReturn(Arrays.asList(mockVacancy)).when(vacancyService).getVacanciesByBroker(mockVacancy.getBroker());

            mockMvc.perform(get("/getJobsByBroker/{broker}", mockVacancy.getBroker()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(Arrays.asList(mockVacancy))));
        }

        @Test
        void getJobsByBrokerNotFound() throws Exception {
            doReturn(new ArrayList<>()).when(vacancyService).getVacanciesByBroker(any(String.class));

            mockMvc.perform(get("/getJobsByBroker/{broker}", "random broker"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(new ArrayList<>())));
        }

        @Test
        void getAllJobs() throws Exception {

            Vacancy mockVacancy2 = vacancyFactory("vacancy2");

            List<Vacancy> vacancies = Arrays.asList(mockVacancy, mockVacancy2);

            doReturn(vacancies).when(vacancyService).getAllVacancies();


            mockMvc.perform(get("/getAllJobs"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(vacancies)));

            // this is meant for integration testing not unit testing, right?
            verify(vacancyService, times(1)).getAllVacancies();
        }

        @Test
        void getJobsWithSkillFound() throws Exception {
            Set<Vacancy> vacancySet = new HashSet<>();
            vacancySet.add(mockVacancy);

            doReturn(vacancySet).when(vacancyService).getVacanciesBySkill(mockSkill.getName());

            mockMvc.perform(get("/getJobsWithSkill/{skill}", mockSkill.getName()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(vacancySet)));
        }

        @Test
        void getJobsWithSkillNotFound() throws Exception {
            doThrow(new SkillNotFoundException("")).when(vacancyService).getVacanciesBySkill(any(String.class));

            mockMvc.perform(get("/getJobsWithSkill/{skill}", "very random skill name"))
                    .andExpect(status().isNotFound());
        }

        @Test
        void deleteVacancyByIdFound() throws Exception {
            //this line is needed when running the vacancy controller test, not when running the test by itself
            //not sure why
            doAnswer(invocationOnMock -> {return null;}).when(vacancyService).delete(mockVacancy.getId());

            mockMvc.perform(delete("/delete/{id}", mockVacancy.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        void deleteVacancyByIdNotFound() throws Exception {
            doThrow(new VacancyNotFoundException("")).when(vacancyService).delete(any(UUID.class));

            mockMvc.perform(delete("/delete/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void replaceVacancyFound() throws Exception {
            Vacancy mockVacancy2 = vacancyFactory("vacancy2");

            doReturn(mockVacancy2).when(vacancyService).replace(mockVacancy.getId(), mockVacancy2);
            mockMvc.perform( put("/{id}", mockVacancy.getId())
                    .content(asJsonString(mockVacancy2))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(mockVacancy2)));
        }

        @Test
        void replaceVacancyNotFound() throws Exception {
            doThrow(new VacancyNotFoundException("")).when(vacancyService).replace(any(UUID.class), any(Vacancy.class));

            mockMvc.perform( put("/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(vacancyFactory("vacancy2"))))
                    .andExpect(status().isNotFound());

        }
    }





    @Nested
    class SkillControllerTest{
        @Test
        void getAllSkills() {
        }
    }

    @Nested
    class SearchControllerTest{
        @Test
        void searchRequest() {
        }
    }

    @Nested
    class ScraperTest{
        @Test
        void scrape() {
        }
    }

    // Maybe replace this with a "result matcher"?
    static private final ObjectMapper objectMapper = new ObjectMapper();
    static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}