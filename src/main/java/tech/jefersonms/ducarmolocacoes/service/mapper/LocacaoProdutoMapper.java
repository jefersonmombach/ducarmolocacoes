package tech.jefersonms.ducarmolocacoes.service.mapper;

import tech.jefersonms.ducarmolocacoes.domain.*;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity LocacaoProduto and its DTO LocacaoProdutoDTO.
 */
@Mapper(componentModel = "spring", uses = {ProdutoMapper.class, LocacaoMapper.class, ClienteMapper.class})
public interface LocacaoProdutoMapper extends EntityMapper<LocacaoProdutoDTO, LocacaoProduto> {

    @Mapping(source = "produto.id", target = "produtoId")
    @Mapping(source = "locacao.id", target = "locacaoId")
    @Mapping(source = "cliente.id", target = "clienteId")
    LocacaoProdutoDTO toDto(LocacaoProduto locacaoProduto);

    @Mapping(source = "produtoId", target = "produto")
    @Mapping(source = "locacaoId", target = "locacao")
    @Mapping(source = "clienteId", target = "cliente")
    LocacaoProduto toEntity(LocacaoProdutoDTO locacaoProdutoDTO);

    default LocacaoProduto fromId(Long id) {
        if (id == null) {
            return null;
        }
        LocacaoProduto locacaoProduto = new LocacaoProduto();
        locacaoProduto.setId(id);
        return locacaoProduto;
    }
}
