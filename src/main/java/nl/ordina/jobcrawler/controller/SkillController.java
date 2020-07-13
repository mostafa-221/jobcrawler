package nl.ordina.jobcrawler.controller;

import lombok.extern.slf4j.Slf4j;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.model.SkillDTO;
import nl.ordina.jobcrawler.service.MatchSkillsService;
import nl.ordina.jobcrawler.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@CrossOrigin
@RestController
public class SkillController {


    private final SkillService skillService;

    private final MatchSkillsService matchSkillsService;

    @Autowired
    public SkillController(SkillService skillService,
                           MatchSkillsService matchSkillsService) {
        this.matchSkillsService = matchSkillsService;
        this.skillService = skillService;
    }

    // getting all skills from database
    @GetMapping(path = "skills")
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

    @GetMapping(path="getskills")
    public List<SkillDTO> getAllMySkills() {
        List<Skill> skills = skillService.getAllSkillsByNameAsc();

        List<SkillDTO> skilldoaList = new ArrayList<>();
        for (Skill s: skills) {
            skilldoaList.add(new SkillDTO(s.getId().toString(), s.getName()));
        }
        return skilldoaList;
    }

    @PostMapping(path="saveskill")
    /* Save a new skill created from the maintenance form in the database.

       This funtion returns HttpStatus OK regardless of the outcome
       Reason is that for example with HttpStatus UNPROCESSABLE_ENTITY, the front end Angular does
       not "see" the error message

       Response code values:
       when no error occurs, error message will be "OK
       when constraint violation occurs "Skill already exists" (a duplicate key is assumed)
       in all other cases, the exception message from the repo
    */
    public ResponseEntity<ResponseCode> saveskill(@RequestBody SkillDTO skillDTO) {
        log.info("save new skill:" + skillDTO.getName());

        try {
            if (skillDTO.getName().length() < 3) {
                return new ResponseEntity<>(new ResponseCode("Name should be longer than 2 characters"),
                        HttpStatus.OK);
            }
            skillService.addSkill(skillDTO.getName());
            return new ResponseEntity<>(new ResponseCode("OK"), HttpStatus.OK);
        } catch (Exception e) {
            ResponseEntity<ResponseCode> errorResult;
            if (e instanceof DataIntegrityViolationException) {
                 errorResult =
                        new ResponseEntity<>(new ResponseCode("Skill already exists"), HttpStatus.OK);
            } else {
                String msg = e.getCause().getMessage();
                errorResult =
                        new ResponseEntity<>(new ResponseCode(msg), HttpStatus.OK);
            }
            return errorResult;
        }
    }


    @PostMapping(path="deleteskill")
    public ResponseEntity<ResponseCode> deleteskill(@RequestBody SkillDTO skillDTO) {
        log.info("delete skill:" + skillDTO.getName());
        try {
            skillService.deleteSkill(skillDTO.getName());
            return new ResponseEntity<>(new ResponseCode("OK"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseCode("Could not delete skill:" + e.getLocalizedMessage()), HttpStatus.OK);
        }
    }

    // getting all skills from database
    @GetMapping(path = "relinkskills")
    public ResponseEntity<ResponseCode> relinkSkills() {
        log.info("relink skills");
        try {
            matchSkillsService.relinkSkills();
            log.info("relink done");
            return new ResponseEntity<>(new ResponseCode("OK"), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ResponseCode("Could not relink skills:" + e.getLocalizedMessage()), HttpStatus.OK);
        }

    }
}
