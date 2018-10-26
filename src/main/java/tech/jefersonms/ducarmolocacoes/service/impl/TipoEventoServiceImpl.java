package tech.jefersonms.ducarmolocacoes.service.impl;

import tech.jefersonms.ducarmolocacoes.service.TipoEventoService;
import tech.jefersonms.ducarmolocacoes.domain.TipoEvento;
import tech.jefersonms.ducarmolocacoes.repository.TipoEventoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.TipoEventoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.dto.TipoEventoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.TipoEventoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing TipoEvento.
 */
@Service
@Transactional
public class TipoEventoServiceImpl implements TipoEventoService {

    private final Logger log = LoggerFactory.getLogger(TipoEventoServiceImpl.class);

    private TipoEventoRepository tipoEventoRepository;

    private TipoEventoMapper tipoEventoMapper;

    private TipoEventoSearchRepository tipoEventoSearchRepository;

    public TipoEventoServiceImpl(TipoEventoRepository tipoEventoRepository, TipoEventoMapper tipoEventoMapper, TipoEventoSearchRepository tipoEventoSearchRepository) {
        this.tipoEventoRepository = tipoEventoRepository;
        this.tipoEventoMapper = tipoEventoMapper;
        this.tipoEventoSearchRepository = tipoEventoSearchRepository;
    }

    /**
     * Save a tipoEvento.
     *
     * @param tipoEventoDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TipoEventoDTO save(TipoEventoDTO tipoEventoDTO) {
        log.debug("Request to save TipoEvento : {}", tipoEventoDTO);

        TipoEvento tipoEvento = tipoEventoMapper.toEntity(tipoEventoDTO);
        tipoEvento = tipoEventoRepository.save(tipoEvento);
        TipoEventoDTO result = tipoEventoMapper.toDto(tipoEvento);
        tipoEventoSearchRepository.save(tipoEvento);
        return result;
    }

    /**
     * Get all the tipoEventos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TipoEventoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TipoEventos");
        return tipoEventoRepository.findAll(pageable)
            .map(tipoEventoMapper::toDto);
    }


    /**
     * Get one tipoEvento by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TipoEventoDTO> findOne(Long id) {
        log.debug("Request to get TipoEvento : {}", id);
        return tipoEventoRepository.findById(id)
            .map(tipoEventoMapper::toDto);
    }

    /**
     * Delete the tipoEvento by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TipoEvento : {}", id);
        tipoEventoRepository.deleteById(id);
        tipoEventoSearchRepository.deleteById(id);
    }

    /**
     * Search for the tipoEvento corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TipoEventoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TipoEventos for query {}", query);
        return tipoEventoSearchRepository.search(queryStringQuery(query), pageable)
            .map(tipoEventoMapper::toDto);
    }
}
