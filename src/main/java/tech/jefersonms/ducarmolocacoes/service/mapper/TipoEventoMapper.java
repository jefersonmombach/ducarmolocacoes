package tech.jefersonms.ducarmolocacoes.service.mapper;

import tech.jefersonms.ducarmolocacoes.domain.*;
import tech.jefersonms.ducarmolocacoes.service.dto.TipoEventoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity TipoEvento and its DTO TipoEventoDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface TipoEventoMapper extends EntityMapper<TipoEventoDTO, TipoEvento> {


    @Mapping(target = "locacoes", ignore = true)
    TipoEvento toEntity(TipoEventoDTO tipoEventoDTO);

    default TipoEvento fromId(Long id) {
        if (id == null) {
            return null;
        }
        TipoEvento tipoEvento = new TipoEvento();
        tipoEvento.setId(id);
        return tipoEvento;
    }
}
