package nl.ordina.jobcrawler.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    This controller is made to purely test the azure deployment
 */
@RestController
@CrossOrigin
@RequestMapping(path = "/test")
public class TestController {
    @GetMapping
    public String testing(){
        return "Hello there! it is working.";
    }
}
