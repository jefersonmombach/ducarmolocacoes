package tech.jefersonms.ducarmolocacoes.service.mapper;

import tech.jefersonms.ducarmolocacoes.domain.*;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Locacao and its DTO LocacaoDTO.
 */
@Mapper(componentModel = "spring", uses = {ClienteMapper.class, TipoEventoMapper.class})
public interface LocacaoMapper extends EntityMapper<LocacaoDTO, Locacao> {

    @Mapping(source = "cliente.id", target = "clienteId")
    @Mapping(source = "tipoEvento.id", target = "tipoEventoId")
    LocacaoDTO toDto(Locacao locacao);

    @Mapping(target = "produtos", ignore = true)
    @Mapping(source = "clienteId", target = "cliente")
    @Mapping(source = "tipoEventoId", target = "tipoEvento")
    Locacao toEntity(LocacaoDTO locacaoDTO);

    default Locacao fromId(Long id) {
        if (id == null) {
            return null;
        }
        Locacao locacao = new Locacao();
        locacao.setId(id);
        return locacao;
    }
}
