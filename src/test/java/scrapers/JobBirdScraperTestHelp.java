package scrapers;

import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.scrapers.HTMLStructureException;
import nl.ordina.jobcrawler.scrapers.JobBirdScraper;
import org.jsoup.nodes.Document;

public class JobBirdScraperTestHelp extends JobBirdScraper {
    public JobBirdScraperTestHelp() {

    }

    public int retrieveTotalNumberOfPagesHelp(Document doc) throws HTMLStructureException {
        return retrieveTotalNumberOfPages(doc);
    }

    public String retrieveVacancyTitleHelp(Document doc) throws HTMLStructureException {
         return retrieveVacancyTitle(doc);
    }

    public Vacancy retrieveVacancySpecificsHelp(Document doc) throws HTMLStructureException {
        Vacancy vacancy = new Vacancy();
        vacancy.setHours(retrieveHoursFromPage(doc));
        vacancy.setLocation(retrieveLocation(doc));
        vacancy.setPostingDate(retrievePublishDate(doc));
        return vacancy;
    }
}
