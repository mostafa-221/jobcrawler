package nl.ordina.jobcrawler.model.assembler;

import nl.ordina.jobcrawler.controller.SkillController;
import nl.ordina.jobcrawler.model.Skill;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SkillModelAssembler implements RepresentationModelAssembler<Skill, EntityModel<Skill>> {
    @Override
    public EntityModel<Skill> toModel(Skill skill) {
        return EntityModel.of(skill,
                linkTo(methodOn(SkillController.class).getSkill(skill.getId())).withSelfRel(),
                linkTo(methodOn(SkillController.class).getSkills()).withRel("skills")
        );
    }
}
