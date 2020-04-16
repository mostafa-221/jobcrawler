package nl.ordina.jobcrawler.controller.exception;


public class VacancyURLMalformedException extends RuntimeException {
    public VacancyURLMalformedException(String url) {
        super("Vacancy URL is malformed or could not be reached " + url);
    }

    public VacancyURLMalformedException(String url, int responseCode) {
        super("Vacancy URL is malformed or could not be reached " + url + " gave code " + responseCode);
    }
}

