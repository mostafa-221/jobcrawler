package nl.ordina.jobcrawler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.Vacancy;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashSet;
import java.util.UUID;

public interface ControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();

    static Vacancy vacancyFactory(String title) {
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

    static Skill skillFactory(String title) {
        return Skill.builder()
                .id(UUID.randomUUID())
                .name(title)
                .vacancies(new HashSet<Vacancy>())
                .build();
    }

    static String asJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
