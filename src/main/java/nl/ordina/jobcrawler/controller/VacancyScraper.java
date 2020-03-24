package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;

import java.io.IOException;
import java.util.List;

// All vacancy scrapers have the getVacancies function in common

public interface VacancyScraper {
    public List<Vacancy> getVacancies() throws IOException;
}
