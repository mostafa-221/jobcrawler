package nl.ordina.jobcrawler.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class VacancyNotFoundException extends RuntimeException {
    public VacancyNotFoundException(String message) {
        super(message);
    }
}
