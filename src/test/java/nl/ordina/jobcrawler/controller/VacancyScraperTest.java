package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class VacancyScraperTest {

    private static VacancyScraper vacancyScraper;

    @BeforeClass
    public static void init() throws IOException {
        vacancyScraper = mock(VacancyScraper.class);
        Vacancy vacancy1 = new Vacancy();
        Vacancy vacancy2 = new Vacancy();
        List<Vacancy> vacancyList = new ArrayList<>();
        vacancyList.add(vacancy1);
        vacancyList.add(vacancy2);

        when(vacancyScraper.getVacancies()).thenReturn(vacancyList);
        when(vacancyScraper.getSEARCH_URL()).thenReturn("https://google.nl");
    }

    @Test
    public void getSEARCH_URL_Test() {
        assertEquals(vacancyScraper.getSEARCH_URL(), "https://google.nl");
    }

    @Test
    public void getVacancies_Test() throws IOException {
        assertNotNull(vacancyScraper.getVacancies());
        assertEquals(vacancyScraper.getVacancies().size(), 2);
    }

    @Test
    public void getDocument_Test() throws IOException {
        Document doc = vacancyScraper.getDocument(vacancyScraper.getSEARCH_URL());
        assertNull(doc);
        verify(vacancyScraper, times(1)).getDocument(vacancyScraper.getSEARCH_URL());
    }

}
