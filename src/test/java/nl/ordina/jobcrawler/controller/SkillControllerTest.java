package nl.ordina.jobcrawler.controller;


import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.service.SkillService;
import nl.ordina.jobcrawler.utils.SkillFactory;
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

@WebMvcTest(SkillController.class)
@DisplayName("/skills")
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;


    @Test
    @DisplayName("GET - OK")
    void testGetAllSkills() throws Exception {
        Skill skill1 = SkillFactory.create("skill1");
        Skill skill2 = SkillFactory.create("skill2");

        List<Skill> skills = Arrays.asList(skill1, skill2);

        doReturn(skills).when(skillService).findAll();

        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(asJsonString(skills)));

    }

    @Nested
    @DisplayName("POST")
    class post {
        @Test
        @DisplayName("POST - OK")
        void testCreateSkillSuccess() throws Exception {
            Skill skill = SkillFactory.create("skill1");

            doReturn(skill).when(skillService).save(any(Skill.class));

            mockMvc.perform(post("/skills")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(skill)))

                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().string(asJsonString(skill)));
        }

        @Test
        @DisplayName("POST - Bad Request")
        void testCreateSkillBadRequest() throws Exception {
            Skill skill = SkillFactory.create("skill1");

            doReturn(skill).when(skillService).save(any(Skill.class));

            mockMvc.perform(post("/skills")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{" +
                            "\"skillURL\":\"example.com\"," +
                            "\"name\":\"skill1789\"," +
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
            void testGetSkillByIdFound() throws Exception {
                Skill skill = SkillFactory.create("skill1");

                doReturn(Optional.of(skill)).when(skillService).findById(skill.getId());

                mockMvc.perform(get("/skills/{id}", skill.getId()))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                        .andExpect(content().string(asJsonString(skill)));

            }

            @Test
            @DisplayName("GET - Not Found")
            void testGetSkillByIDNotFound() throws Exception {

                doReturn(Optional.empty()).when(skillService).findById(any(UUID.class));

                mockMvc.perform(get("/getByID/{id}", UUID.randomUUID()))
                        .andExpect(status().isNotFound());
            }
        }

        @Disabled   // as skills are immutable, they cannot be updated
        @Nested
        @DisplayName("PUT")
        class put {
            @Test
            @DisplayName("PUT - Not Found")
            void testUpdateSkillNotFound() throws Exception {
                Skill skill = SkillFactory.create("skill1");
                Skill newSkill = SkillFactory.create("skill2");

//                doReturn(Optional.empty()).when(skillService).replace(skill.getId(), newSkill);

                mockMvc.perform(put("/skills/{id}", UUID.randomUUID())
                        .content(asJsonString(newSkill))
                        .contentType(MediaType.APPLICATION_JSON))

                        .andExpect(status().isNotFound());
            }

            /* TODO: Not sure what we want to do with versioning
            maybe we can have immutability for skills */
            @Test
            @DisplayName("PUT - Conflict")
            void testUpdateSkillConflict() throws Exception {
                Skill skill = SkillFactory.create("skill1");
                Skill newSkill = SkillFactory.create("skill2");

//                doReturn(Optional.empty()).when(skillService).replace(skill.getId(), newSkill);

                mockMvc.perform(put("/skills/{id}", skill)
                        .content(asJsonString(newSkill))
                        .contentType(MediaType.APPLICATION_JSON)

                        .header(HttpHeaders.IF_MATCH, 12345))
                        .andExpect(status().isConflict());
            }

            @Test
            @DisplayName("PUT - OK")
            void testUpdateSkillSuccess() throws Exception {
                Skill skill = SkillFactory.create("skill1");
                Skill newSkill = SkillFactory.create("skill2");

//                doReturn(Optional.empty()).when(skillService).replace(skill.getId(), newSkill);

                mockMvc.perform(put("/skills/{id}", skill)
                        .content(asJsonString(newSkill))
                        .contentType(MediaType.APPLICATION_JSON))

                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(content().string(asJsonString(newSkill)));
            }
        }

        @Nested
        @DisplayName("DELETE")
        class delete {
            @Test
            @DisplayName("DELETE - OK")
            void deleteSkillByIdFound() throws Exception {

                Skill skill = SkillFactory.create("skill");
                doReturn(Optional.of(skill)).when(skillService).findById(skill.getId());
                doReturn(true).when(skillService).delete(skill.getId());

                mockMvc.perform(delete("/skills/{id}", skill.getId()))
                        .andExpect(status().isOk());
            }

            @Test
            @DisplayName("DELETE - Not Found")
            void deleteSkillByIdNotFound() throws Exception {

                Skill skill = SkillFactory.create("skill");

                doReturn(Optional.empty()).when(skillService).findById(any(UUID.class));

                mockMvc.perform(delete("/skills/{id}", skill.getId()))
                        .andExpect(status().isNotFound());
            }
        }


    }


}
