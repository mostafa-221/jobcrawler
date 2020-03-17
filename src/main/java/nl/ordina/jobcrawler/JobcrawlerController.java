package nl.ordina.jobcrawler;

import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicLong;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class JobcrawlerController {

    private final AtomicLong counter = new AtomicLong();

    @PostMapping("/searchrequest")
    public SearchResult searchRequest(@RequestBody SearchRequest request) {

        // Temporary mock data
        String requestText = "For request: " + request.getLocation() + ", " + request.getDistance() + ", " + request.getKeywords() +  ":";
        String[] texts = {requestText, "Test result 1", "Test result 2"};
        String[] links = {"http://google.com/", "http://duckduckgo.com/"};

        return new SearchResult(texts, links);

    }

}