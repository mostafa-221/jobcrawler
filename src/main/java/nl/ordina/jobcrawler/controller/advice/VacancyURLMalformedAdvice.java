package nl.ordina.jobcrawler.controller.advice;

import nl.ordina.jobcrawler.controller.exception.VacancyURLMalformedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class VacancyURLMalformedAdvice {

    @ResponseBody
    @ExceptionHandler(VacancyURLMalformedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
        //not sure if this is the correct status
    String vacancyURLMalformedHandler(VacancyURLMalformedException ex) {
        return ex.getMessage();
    }
}
