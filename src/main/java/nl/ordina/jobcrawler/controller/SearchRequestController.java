package nl.ordina.jobcrawler.controller;

import nl.ordina.jobcrawler.SearchResult;
import nl.ordina.jobcrawler.model.Vacancy;
import nl.ordina.jobcrawler.service.VacancyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin
@RestController
public class SearchRequestController {

    private final VacancyService vacancyService;

    @Autowired
    public SearchRequestController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @GetMapping("/vacancies")
    public ResponseEntity<SearchResult> getVacancies(
            @RequestParam(required = false) String value,
            @RequestParam(required = false) Set<String> skills,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            List<Vacancy> vacancyList = new ArrayList<>();
            Pageable paging = PageRequest.of(page, size);

            Page<Vacancy> vacancies;

            if (value != null && !value.isBlank())
                vacancies = vacancyService.findByAnyValue(value, paging);
            else if(skills != null && !skills.isEmpty())
                vacancies = vacancyService.findBySkills(skills, paging);
            else
                vacancies = vacancyService.findAll(paging);
            vacancyList = vacancies.getContent();

            if (vacancyList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            SearchResult searchResult = new SearchResult();
            searchResult.setVacancies(vacancyList);
            searchResult.setCurrentPage(vacancies.getNumber());
            searchResult.setTotalItems(vacancies.getTotalElements());
            searchResult.setTotalPages(vacancies.getTotalPages());
            return new ResponseEntity<>(searchResult, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
