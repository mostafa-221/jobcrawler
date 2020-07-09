package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.SearchRequest;
import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class SearchRequestController {

    private final VacancyService vacancyService;

    @Autowired
    public SearchRequestController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @PostMapping("/searchrequest")
    public SearchResult searchRequest(@RequestBody SearchRequest request) {
        return new SearchResult(request, vacancyService.findAll());
    }
}
