package nl.ordina.jobcrawler;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private final AtomicLong counter = new AtomicLong();

    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam String name) {
        return new Greeting(counter.incrementAndGet(), name);
    }

}