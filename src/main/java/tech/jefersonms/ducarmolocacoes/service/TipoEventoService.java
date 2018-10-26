package tech.jefersonms.ducarmolocacoes.service;

import tech.jefersonms.ducarmolocacoes.service.dto.TipoEventoDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing TipoEvento.
 */
public interface TipoEventoService {

    /**
     * Save a tipoEvento.
     *
     * @param tipoEventoDTO the entity to save
     * @return the persisted entity
     */
    TipoEventoDTO save(TipoEventoDTO tipoEventoDTO);

    /**
     * Get all the tipoEventos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TipoEventoDTO> findAll(Pageable pageable);


    /**
     * Get the "id" tipoEvento.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TipoEventoDTO> findOne(Long id);

    /**
     * Delete the "id" tipoEvento.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the tipoEvento corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TipoEventoDTO> search(String query, Pageable pageable);
}
