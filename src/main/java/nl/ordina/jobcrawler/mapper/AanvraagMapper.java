package nl.ordina.jobcrawler.mapper;

import nl.ordina.jobcrawler.model.Aanvraag;
import nl.ordina.jobcrawler.persistence.dto.AanvraagDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AanvraagMapper {
    AanvraagDTO toAanvraagDTO(Aanvraag aanvraag);

    List<AanvraagDTO> toAanvraagDTOs(List<Aanvraag> aanvraag);

    Aanvraag toAanvraag(AanvraagDTO aanvraagDTO);
}
