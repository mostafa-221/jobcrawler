package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import org.jsoup.nodes.Document;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VacancyScraperTest {

    private static VacancyScraper vacancyScraper;

    private static Vacancy vacancy1;
    private static Vacancy vacancy2;

    @BeforeClass
    public static void init() throws IOException {
        vacancyScraper = mock(VacancyScraper.class);
        vacancy1 = new Vacancy();
        vacancy2 = new Vacancy();
        List<Vacancy> vacancyList = new ArrayList<>();
        vacancyList.add(vacancy1);
        vacancyList.add(vacancy2);

        when(vacancyScraper.getVacancies()).thenReturn(vacancyList);
        when(vacancyScraper.getTotalNumberOfPages(Mockito.any())).thenReturn(1);
        when(vacancyScraper.getSEARCH_URL()).thenReturn("https://google.nl");
    }

    @Test
    public void getVacancies_Test() throws IOException {
        assertNotNull(vacancyScraper.getVacancies());
        assertEquals(vacancyScraper.getVacancies().size(), 2);
    }

    @Test
    public void getSEARCH_URL_Test() {
        assertEquals(vacancyScraper.getSEARCH_URL(), "https://google.nl");
    }

    @Test
    public void getTotalNumberOfPages_Test() {
        assertNotNull(vacancyScraper.getTotalNumberOfPages(Mockito.any()));
        assertEquals(vacancyScraper.getTotalNumberOfPages(Mockito.any()), 1);
    }
}
