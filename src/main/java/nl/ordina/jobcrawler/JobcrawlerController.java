package nl.ordina.jobcrawler;

import nl.ordina.jobcrawler.model.Aanvraag;
import nl.ordina.jobcrawler.service.AanvraagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class JobcrawlerController {

    @Autowired
    private AanvraagService aanvraagService;

    @PostMapping("/searchrequest")
    public SearchResult searchRequest(@RequestBody SearchRequest request) {

        // Temporary mock data
        String requestText = "For request: " + request.getLocation() + ", " + request.getDistance() + ", " + request.getKeywords() +  ":";
        String[] texts = {requestText, "Test result 1", "Test result 2"};
        String[] links = {"http://google.com/", "http://duckduckgo.com/"};

        return new SearchResult(request, aanvraagService.getAllJobs());

    }

    @GetMapping("/add")
    public void addTest() {
        // Add 10 random entries with different skillsets into the database
        for(int i = 0; i<10; i++) {
            Aanvraag aanvraag = new Aanvraag();
            aanvraag.setAanvraagURL("google.nl");
            List<String> skillset = new ArrayList<>();

            for(int j = 0; j < 3; j++) { // add random skills to List<String>
                Random r1 = new Random();
                char c1 = (char) (r1.nextInt(26) + 'a');
                String s1 = String.valueOf(c1);
                skillset.add(s1);
            }
            aanvraag.setSkillSet(skillset);

            aanvraagService.add(aanvraag);
        }
    }

    @GetMapping("/getAllJobs")
    public List<Aanvraag> getAllJobs() {
        // Retrieve all 'aanvragen' that are available in the database
        return aanvraagService.getAllJobs();
    }

    @GetMapping("/getJobsWithSkill/{skill}")
    public List<Aanvraag> getJobsWithSkill(@PathVariable("skill") String skill) {
        // Only show 'aanvragen' which needs a specific skill that's requested via a get method (localhost:8080/getJobsWithSkill/requestedskill )
        return aanvraagService.getJobsWithSkill(skill);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteAanvraagById(@PathVariable("id") UUID id) {
        // Delete aanvraag by id (UUID)
        aanvraagService.delete(id);
    }

}