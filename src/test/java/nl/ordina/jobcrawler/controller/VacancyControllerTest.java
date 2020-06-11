package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
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

import static nl.ordina.jobcrawler.controller.ControllerTest.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        Vacancy vacancy1 = vacancyFactory("job1");
        Vacancy vacancy2 = vacancyFactory("job2");

        List<Vacancy> vacancies = Arrays.asList(vacancy1, vacancy2);

        doReturn(vacancies).when(vacancyService).getAllVacancies();

        mockMvc.perform(get("/vacancies"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(vacancies)));

    }

    @Test
    @DisplayName("POST - OK")
    void testCreateVacancySuccess() throws Exception {
        Vacancy vacancy = vacancyFactory("job1");

        doReturn(vacancy).when(vacancyService).add(any(Vacancy.class));

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
        Vacancy vacancy = vacancyFactory("job1");

        doReturn(vacancy).when(vacancyService).add(any(Vacancy.class));

        mockMvc.perform(post("/vacancies")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"invalidVacancyAttribute\": \"6t5\" }")
                .content(asJsonString(vacancy)))

                .andExpect(status().isBadRequest());
    }


    @Nested
    @DisplayName("/{id}")
    class id {

        @Test
        @DisplayName("GET - OK")
        void testGetVacancyByIdFound() throws Exception {
            Vacancy vacancy = vacancyFactory("job1");

            doReturn(Optional.of(vacancy)).when(vacancyService).getVacancyByID(vacancy.getId());

            mockMvc.perform(get("/vacancies/{id}", vacancy.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                    .andExpect(content().string(asJsonString(vacancy)));

        }

        @Test
        @DisplayName("GET - Not Found")
        void testGetVacancyByIDNotFound() throws Exception {

            doReturn(Optional.empty()).when(vacancyService).getVacancyByID(any(UUID.class));

            mockMvc.perform(get("/getByID/{id}", UUID.randomUUID()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("PUT - Not Found")
        void testUpdateVacancyNotFound() throws Exception {
            Vacancy vacancy = vacancyFactory("job1");
            Vacancy newVacancy = vacancyFactory("job2");

            doReturn(Optional.empty()).when(vacancyService).replace(vacancy.getId(), newVacancy);

            mockMvc.perform(put("/vacancies/{id}", UUID.randomUUID())
                    .content(asJsonString(newVacancy))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isNotFound());
        }

        /* TODO: Not sure what we want to do with versioning
        maybe we can have immutability for vacancies */
        @Disabled
        @Test
        @DisplayName("PUT - Conflict")
        void testUpdateVacancyConflict() throws Exception {
            Vacancy vacancy = vacancyFactory("job1");
            Vacancy newVacancy = vacancyFactory("job2");

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
            Vacancy vacancy = vacancyFactory("job1");
            Vacancy newVacancy = vacancyFactory("job2");

            doReturn(Optional.empty()).when(vacancyService).replace(vacancy.getId(), newVacancy);

            mockMvc.perform(put("/vacancies/{id}", vacancy)
                    .content(asJsonString(newVacancy))
                    .contentType(MediaType.APPLICATION_JSON))

                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(newVacancy)));
        }

        @Test
        @DisplayName("DELETE - OK")
        void deleteVacancyByIdFound() throws Exception {

            Vacancy vacancy = vacancyFactory("job");

            doNothing().when(vacancyService).delete(vacancy.getId());

            mockMvc.perform(delete("/vacancies/{id}", vacancy.getId()))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("DELETE - Not Found")
        void deleteVacancyByIdNotFound() throws Exception {

            Vacancy vacancy = vacancyFactory("job");

            doThrow(new VacancyNotFoundException("")).when(vacancyService).delete(any(UUID.class));

            mockMvc.perform(delete("/vacancies/{id}", vacancy.getId()))
                    .andExpect(status().isNotFound());
        }

    }


}

