package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.service.SkillService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SkillController.class)
class SkillControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SkillService skillService;

    private Skill mockSkill;


    /**
     * Tests the /skills end point, if it returns a list of vacancies
     */
    @Test
    void getAllSkills() throws Exception {
        Skill mockSkill2 = ControllerTest.skillFactory("skill2");

        List<Skill> skills = Arrays.asList(mockSkill, mockSkill2);

        doReturn(skills).when(skillService).getAllSkills();

        mockMvc.perform(get("/skills"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(ControllerTest.asJsonString(skills)));
    }
}