package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VacancyController.class)
class VacancyControllerTest implements ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService vacancyService;

    private Vacancy mockVacancy;
    private Skill mockSkill;


    @BeforeEach
    void setUp() {
        mockVacancy = ControllerTest.vacancyFactory("job1");
        mockSkill = ControllerTest.skillFactory("skill1");

        mockVacancy.addSkill(mockSkill);
    }

    @Test
    /**
     * Tests the /addJobWithJson end point, if it adds a vacancy correctly
     */
    void addJob() throws Exception {
        doReturn(mockVacancy).when(vacancyService).add(any(Vacancy.class));

        mockMvc.perform(post("/addJobWithJson")
                .contentType(MediaType.APPLICATION_JSON)
                .content(ControllerTest.asJsonString(mockVacancy)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(ControllerTest.asJsonString(mockVacancy)));

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

                .andExpect(content().string(ControllerTest.asJsonString(mockVacancy)));

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
                .andExpect(content().string(ControllerTest.asJsonString(Arrays.asList(mockVacancy))));
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
                .andExpect(content().string(ControllerTest.asJsonString(new ArrayList<>())));
    }

    /**
     * Tests the /getAllJobs end point, if it returns a list of vacancies
     */
    @Test
    void getAllJobs() throws Exception {

        Vacancy mockVacancy2 = ControllerTest.vacancyFactory("vacancy2");

        List<Vacancy> vacancies = Arrays.asList(mockVacancy, mockVacancy2);

        doReturn(vacancies).when(vacancyService).getAllVacancies();


        mockMvc.perform(get("/getAllJobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(ControllerTest.asJsonString(vacancies)));

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
                .andExpect(content().string(ControllerTest.asJsonString(vacancySet)));
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
        Vacancy mockVacancy2 = ControllerTest.vacancyFactory("vacancy2");

        doReturn(mockVacancy2).when(vacancyService).replace(mockVacancy.getId(), mockVacancy2);
        mockMvc.perform(put("/{id}", mockVacancy.getId())
                .content(ControllerTest.asJsonString(mockVacancy2))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(ControllerTest.asJsonString(mockVacancy2)));
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
                .content(ControllerTest.asJsonString(ControllerTest.vacancyFactory("vacancy2"))))
                .andExpect(status().isNotFound());

    }

}

