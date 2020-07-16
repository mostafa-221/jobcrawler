package nl.ordina.jobcrawler.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.service.SkillMatcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin
@RestController
public class SkillMatcherController {


    private final SkillMatcherService skillMatcherService;

    @Autowired
    public SkillMatcherController(
            SkillMatcherService skillMatcherService) {
        this.skillMatcherService = skillMatcherService;
    }



    // rematch the skills
    @PutMapping(path = "skillmatcher")
    public ResponseEntity<ResponseCode> relinkSkills() {
        log.info("relink skills");
        try {
            skillMatcherService.relinkSkills();
            log.info("relink done");
            return new ResponseEntity<>(new ResponseCode("OK"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseCode("Could not relink skills:" + e.getLocalizedMessage()), HttpStatus.OK);
        }

    }
}
