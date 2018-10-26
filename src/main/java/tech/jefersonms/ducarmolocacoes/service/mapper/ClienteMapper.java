package tech.jefersonms.ducarmolocacoes.service.mapper;

import tech.jefersonms.ducarmolocacoes.domain.*;
import tech.jefersonms.ducarmolocacoes.service.dto.ClienteDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Cliente and its DTO ClienteDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface ClienteMapper extends EntityMapper<ClienteDTO, Cliente> {


    @Mapping(target = "locacoes", ignore = true)
    @Mapping(target = "produtos", ignore = true)
    Cliente toEntity(ClienteDTO clienteDTO);

    default Cliente fromId(Long id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setId(id);
        return cliente;
    }
}
