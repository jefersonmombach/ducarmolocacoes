package tech.jefersonms.ducarmolocacoes.service;

import org.springframework.transaction.annotation.Transactional;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tech.jefersonms.ducarmolocacoes.service.dto.ProdutoDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Interface for managing Locacao.
 */
public interface LocacaoService {

    /**
     * Save a locacao.
     *
     * @param locacaoDTO the entity to save
     * @return the persisted entity
     */
    LocacaoDTO save(LocacaoDTO locacaoDTO);

    /**
     * Get all the locacaos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LocacaoDTO> findAll(Pageable pageable);


    /**
     * Get the "id" locacao.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<LocacaoDTO> findOne(Long id);

    @Transactional(readOnly = true)
    List<LocacaoDTO> listLocacoesProdutoByDataEvento(ProdutoDTO produto, LocalDate dataEvento);

    /**
     * Delete the "id" locacao.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Patch the "id" locacao.
     *
     * @param id the id of the entity
     */
    LocacaoDTO patch(Long id, Map<String, String> fields);

    /**
     * Search for the locacao corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<LocacaoDTO> search(String query, Pageable pageable);
}
