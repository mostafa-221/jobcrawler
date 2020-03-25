package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.model.VacancyURLs;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

// All vacancy scrapers have the following functions in common

abstract class VacancyScraper {

    protected static final String SEARCH_TERM = "java";

    abstract public List<Vacancy> getVacancies() throws IOException;

    abstract protected List<VacancyURLs> getVacancyURLs() throws IOException;

    abstract protected int getTotalNumberOfPages(Document doc);

    abstract protected void setVacancyTitle(Document doc, Vacancy vacancy);

    abstract protected void setVacancySpecifics(Document doc, Vacancy vacancy);

    abstract protected List<String> getVacancySpecifics(Document doc);

    abstract protected void setVacancyAbout(Document doc, Vacancy vacancy);

    abstract protected void setVacancySkillSet(Document doc, Vacancy vacancy);

}
