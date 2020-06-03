package nl.ordina.jobcrawler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.SearchRequest;
import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.SkillService;
import nl.ordina.jobcrawler.service.VacancyService;
import nl.ordina.jobcrawler.service.VacancyStarter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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

    private static Vacancy vacancyFactory(final String title) {
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

    private static Skill skillFactory(final String title) {
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

    // TODO: make comments
    // TODO: make a list of all the improvements

    @Nested
    class VacancyControllerTest {
        @Test
        /**
         * Tests the /addJobWithJson end point, if it adds a vacancy correctly
         */
        void addJob() throws Exception {
            doReturn(mockVacancy).when(vacancyService).add(any(Vacancy.class));

            mockMvc.perform(post("/addJobWithJson")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(mockVacancy)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(mockVacancy)));

        }

        /**
         * Tests the /getByID end point, when a vacancy is found
         */
        @Test
        void getByIDFound() throws Exception {

            doReturn(Optional.of(mockVacancy)).when(vacancyService).getVacancyByID(mockVacancy.getId());

            ResultActions result = mockMvc.perform(get("/getByID/{id}", mockVacancy.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    .andExpect(content().string(asJsonString(mockVacancy)));

        }

        /**
         * Tests the /getByID end point, when a vacancy is not found
         */
        @Test
        void getByIDNotFound() throws Exception {

            doReturn(Optional.empty()).when(vacancyService).getVacancyByID(any(UUID.class));

            mockMvc.perform(get("/getByID/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());

        }

        /**
         * Tests the /getJobsByBroker end point, when a broker is found
         */
        @Test
        void getJobsByBroker() throws Exception {
            doReturn(Arrays.asList(mockVacancy)).when(vacancyService).getVacanciesByBroker(mockVacancy.getBroker());

            mockMvc.perform(get("/getJobsByBroker/{broker}", mockVacancy.getBroker()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(Arrays.asList(mockVacancy))));
        }

        /**
         * Tests the /getJobsByBroker end point, when a broker is not found
         */
        @Test
        void getJobsByBrokerNotFound() throws Exception {
            doReturn(new ArrayList<>()).when(vacancyService).getVacanciesByBroker(any(String.class));

            mockMvc.perform(get("/getJobsByBroker/{broker}", "random broker"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(new ArrayList<>())));
        }

        /**
         * Tests the /getAllJobs end point, if it returns a list of vacancies
         */
        @Test
        void getAllJobs() throws Exception {

            Vacancy mockVacancy2 = vacancyFactory("vacancy2");

            List<Vacancy> vacancies = Arrays.asList(mockVacancy, mockVacancy2);

            doReturn(vacancies).when(vacancyService).getAllVacancies();


            mockMvc.perform(get("/getAllJobs"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(vacancies)));

        }

        /**
         * Tests the /getJobsWithSkill end point, when a skill is found
         */
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

        /**
         * Tests the /getJobsWithSkill end point, when a skill is not found
         */
        @Test
        void getJobsWithSkillNotFound() throws Exception {
            doThrow(new SkillNotFoundException("")).when(vacancyService).getVacanciesBySkill(any(String.class));

            mockMvc.perform(get("/getJobsWithSkill/{skill}", "very random skill name"))
                    .andExpect(status().isNotFound());
        }

        /**
         * Tests the /delete end point, when an id is found
         */
        @Test
        void deleteVacancyByIdFound() throws Exception {
            //this line is needed when running the vacancy controller test, not when running the test by itself
            //not sure why
//            doAnswer(invocationOnMock -> {
//                return null;
//            }).when(vacancyService).delete(mockVacancy.getId());
            doNothing().when(vacancyService).delete(mockVacancy.getId());

            mockMvc.perform(delete("/delete/{id}", mockVacancy.getId()))
                    .andExpect(status().isOk());
        }

        /**
         * Tests the /delete end point, when an id is not found
         */
        @Test
        void deleteVacancyByIdNotFound() throws Exception {
            doThrow(new VacancyNotFoundException("")).when(vacancyService).delete(any(UUID.class));

            mockMvc.perform(delete("/delete/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }

        /**
         * Tests the /{id} end point, when an id is found
         * this end point is for updating the vacancies
         */
        @Test
        void replaceVacancyFound() throws Exception {
            Vacancy mockVacancy2 = vacancyFactory("vacancy2");

            doReturn(mockVacancy2).when(vacancyService).replace(mockVacancy.getId(), mockVacancy2);
            mockMvc.perform(put("/{id}", mockVacancy.getId())
                    .content(asJsonString(mockVacancy2))
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(mockVacancy2)));
        }

        /**
         * Tests the /{id} end point, when an id is not found
         * this end point is for updating the vacancies
         */
        @Test
        void replaceVacancyNotFound() throws Exception {
            doThrow(new VacancyNotFoundException("")).when(vacancyService).replace(any(UUID.class), any(Vacancy.class));

            mockMvc.perform(put("/{id}", UUID.randomUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(vacancyFactory("vacancy2"))))
                    .andExpect(status().isNotFound());

        }
    }


    @Nested
    class SkillControllerTest {

        /**
         * Tests the /skills end point, if it returns a list of vacancies
         */
        @Test
        void getAllSkills() throws Exception {
            Skill mockSkill2 = skillFactory("skill2");

            List<Skill> skills = Arrays.asList(mockSkill, mockSkill2);

            doReturn(skills).when(skillService).getAllSkills();

            mockMvc.perform(get("/skills"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(skills)));
        }
    }

    @Nested
    class SearchControllerTest {

        /**
         * Tests the /searchrequest end point,
         * for now all it needs to do is to return a new SearchResult with all vacancies
         */
        @Test
        void searchRequest() throws Exception {
            Vacancy mockVacancy2 = vacancyFactory("vacancy2");
            List<Vacancy> vacancies = Arrays.asList(mockVacancy, mockVacancy2);
            doReturn(vacancies).when(vacancyService).getAllVacancies();

            mockMvc.perform(post("/searchrequest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(new SearchRequest())))
                    .andExpect(status().isOk())
                    .andExpect(content().string(asJsonString(new SearchResult(new SearchRequest(), vacancies))));
        }
    }

    @Nested
    class ScraperTest {

        /**
         * Tests the /scrape end point, if it calls the VacancyStarter scrape method
         */
        @Test
        void scrape() throws Exception {
            mockMvc.perform(put("/scrape"))
                    .andExpect(status().isOk());

            verify(vacancyStarter, times(1)).scrape();
        }
    }

    // Maybe replace this with a "result matcher"?
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}