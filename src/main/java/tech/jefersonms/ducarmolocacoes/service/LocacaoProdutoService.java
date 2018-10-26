package tech.jefersonms.ducarmolocacoes.service;

import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing LocacaoProduto.
 */
public interface LocacaoProdutoService {

    /**
     * Save a locacaoProduto.
     *
     * @param locacaoProdutoDTO the entity to save
     * @return the persisted entity
     */
    LocacaoProdutoDTO save(LocacaoProdutoDTO locacaoProdutoDTO);

    /**
     * Get all the locacaoProdutos.
     *
     * @return the list of entities
     */
    List<LocacaoProdutoDTO> findAll();


    /**
     * Get the "id" locacaoProduto.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<LocacaoProdutoDTO> findOne(Long id);

    /**
     * Delete the "id" locacaoProduto.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the locacaoProduto corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<LocacaoProdutoDTO> search(String query);
}
