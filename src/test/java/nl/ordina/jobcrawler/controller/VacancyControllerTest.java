package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import nl.ordina.jobcrawler.utils.VacancyFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.ordina.jobcrawler.utils.Utils.asJsonString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VacancyController.class)
@DisplayName("/vacancies")
class VacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService vacancyService;


    @Test
    @DisplayName("GET - OK")
    void testGetAllVacancies() throws Exception {
        Vacancy vacancy1 = VacancyFactory.create("job1");
        Vacancy vacancy2 = VacancyFactory.create("job2");

        List<Vacancy> vacancies = Arrays.asList(vacancy1, vacancy2);

        doReturn(vacancies).when(vacancyService).findAll();

        mockMvc.perform(get("/vacancies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(vacancies)));

    }

    @Nested
    @DisplayName("POST")
    class post {
        @Test
        @DisplayName("POST - OK")
        void testCreateVacancySuccess() throws Exception {
            Vacancy vacancy = VacancyFactory.create("job1");

            doReturn(vacancy).when(vacancyService).save(any(Vacancy.class));

            mockMvc.perform(post("/vacancies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(vacancy)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(vacancy)));
        }

        @Test
        @DisplayName("POST - Bad Request")
        void testCreateVacancyBadRequest() throws Exception {
            Vacancy vacancy = VacancyFactory.create("job1");

            doReturn(vacancy).when(vacancyService).save(any(Vacancy.class));

            mockMvc.perform(post("/vacancies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{" +
                            "\"vacancyURL\":\"example.com\"," +
                            "\"title\":\"job1\"," +
                            "\"broker\":\"broker\"," +
                            "\"vacancyNumber\":\"1\"," +
                            "\"hours\":\"30\"," +
                            "\"location\":\"location example\"," +
                            "\"salary\":null," +
                            "\"postingDate\":\"14 April 2020\"," +
                            "\"about\":\"this is a description of the example job\"," +
                            "\"skills\":[]" +
                            "\"invalidattribute\": \"test\"" +
                            "}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("/{id}")
    class id {

        @Nested
        @DisplayName("GET")
        class get {
            @Test
            @DisplayName("GET - OK")
            void testGetVacancyByIdFound() throws Exception {
                Vacancy vacancy = VacancyFactory.create("job1");

                doReturn(Optional.of(vacancy)).when(vacancyService).findById(vacancy.getId());

                mockMvc.perform(get("/vacancies/{id}", vacancy.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                        .andExpect(content().string(asJsonString(vacancy)));

            }

            @Test
            @DisplayName("GET - Not Found")
            void testGetVacancyByIDNotFound() throws Exception {

                doReturn(Optional.empty()).when(vacancyService).findById(any(UUID.class));

                mockMvc.perform(get("/getByID/{id}", UUID.randomUUID()))
                        .andExpect(status().isNotFound());
            }
        }

        @Disabled
        @Nested
        @DisplayName("PUT")
        class put {
            @Test
            @DisplayName("PUT - Not Found")
            void testUpdateVacancyNotFound() throws Exception {
                Vacancy vacancy = VacancyFactory.create("job1");
                Vacancy newVacancy = VacancyFactory.create("job2");

                doReturn(Optional.empty()).when(vacancyService).replace(vacancy.getId(), newVacancy);

                mockMvc.perform(put("/vacancies/{id}", UUID.randomUUID())
                        .content(asJsonString(newVacancy))
                        .contentType(MediaType.APPLICATION_JSON))

                        .andExpect(status().isNotFound());
            }

            /* TODO: Not sure what we want to do with versioning
            maybe we can have immutability for vacancies */
            @Test
            @DisplayName("PUT - Conflict")
            void testUpdateVacancyConflict() throws Exception {
                Vacancy vacancy = VacancyFactory.create("job1");
                Vacancy newVacancy = VacancyFactory.create("job2");

                doReturn(Optional.empty()).when(vacancyService).replace(vacancy.getId(), newVacancy);

                mockMvc.perform(put("/vacancies/{id}", vacancy)
                        .content(asJsonString(newVacancy))
                        .contentType(MediaType.APPLICATION_JSON)

                        .header(HttpHeaders.IF_MATCH, 12345))
                        .andExpect(status().isConflict());
            }

            @Test
            @DisplayName("PUT - OK")
            void testUpdateVacancySuccess() throws Exception {
                Vacancy vacancy = VacancyFactory.create("job1");
                Vacancy newVacancy = VacancyFactory.create("job2");

                doReturn(Optional.empty()).when(vacancyService).replace(vacancy.getId(), newVacancy);

                mockMvc.perform(put("/vacancies/{id}", vacancy)
                        .content(asJsonString(newVacancy))
                        .contentType(MediaType.APPLICATION_JSON))

                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string(asJsonString(newVacancy)));
            }
        }

        @Nested
        @DisplayName("DELETE")
        class delete {
            @Test
            @DisplayName("DELETE - OK")
            void deleteVacancyByIdFound() throws Exception {

                Vacancy vacancy = VacancyFactory.create("job");
                doReturn(Optional.of(vacancy)).when(vacancyService).findById(vacancy.getId());
                doReturn(true).when(vacancyService).delete(vacancy.getId());

                mockMvc.perform(delete("/vacancies/{id}", vacancy.getId()))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("DELETE - Not Found")
            void deleteVacancyByIdNotFound() throws Exception {

                Vacancy vacancy = VacancyFactory.create("job");

                doReturn(Optional.empty()).when(vacancyService).findById(any(UUID.class));

                mockMvc.perform(delete("/vacancies/{id}", vacancy.getId()))
                        .andExpect(status().isNotFound());
            }
        }


    }


}

