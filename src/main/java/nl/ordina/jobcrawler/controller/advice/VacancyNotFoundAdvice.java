package nl.ordina.jobcrawler.controller.advice;

import nl.ordina.jobcrawler.controller.exception.VacancyNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class VacancyNotFoundAdvice {

    @ResponseBody
    @ExceptionHandler(VacancyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String jobNotFoundHandler(VacancyNotFoundException ex) {
        return ex.getMessage();
    }
}
