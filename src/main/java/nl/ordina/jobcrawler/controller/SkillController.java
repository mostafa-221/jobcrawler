package nl.ordina.jobcrawler.controller;


import nl.ordina.jobcrawler.controller.exception.SkillNotFoundException;
import nl.ordina.jobcrawler.model.Skill;
import nl.ordina.jobcrawler.service.SkillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    @Autowired
    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    /**
     * Returns all skills in the database.
     *
     * @return All skills in the database.
     */
    @GetMapping
    public Iterable<Skill> getSkills() {
        return skillService.findAll();
    }


    /**
     * Creates a new skill.
     *
     * @param skill The skill to create.
     * @return The created skill and code 201 Created
     * Code 400 Bad Request if the given body is invalid
     */
    @PostMapping
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skill) {
        Skill returnedSkill = skillService.save(skill);
        try {
            return ResponseEntity
                    .created(new URI("/skills/" + returnedSkill.getId()))
                    .body(returnedSkill);
        } catch (URISyntaxException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    /**
     * Returns the skill with the specified ID.
     *
     * @param id The ID of the skill to retrieve.
     * @return The skill with the specified ID, or code 404 Not Found if the id was not found.
     * @throws SkillNotFoundException when a skill is not found with the specified ID.
     */
    @GetMapping("/{id}")
    public Skill getSkill(@PathVariable UUID id) {
        return skillService.findById(id)
                .orElseThrow(() -> new SkillNotFoundException(id));
    }

    @PutMapping("/{id}")
    public Skill updateSkill(@PathVariable UUID id, @RequestBody Skill skill) {
        skillService.findById(id).orElseThrow(() -> new SkillNotFoundException(id));
        return skillService.update(id, skill);
    }

    /**
     * Deletes the skill with the specified ID.
     *
     * @param id The ID of the skill to delete.
     * @return A ResponseEntity with one of the following status codes:
     * 200 OK if the delete was successful
     * 404 Not Found if a skill with the specified ID is not found
     */
    @DeleteMapping("/{id}")
    public boolean deleteSkill(@PathVariable UUID id) {
        skillService.findById(id).orElseThrow(() -> new SkillNotFoundException(id));
        return skillService.delete(id);
    }


}

