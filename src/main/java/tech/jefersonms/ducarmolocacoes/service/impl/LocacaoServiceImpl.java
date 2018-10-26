package tech.jefersonms.ducarmolocacoes.service.impl;

import tech.jefersonms.ducarmolocacoes.service.LocacaoService;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Locacao.
 */
@Service
@Transactional
public class LocacaoServiceImpl implements LocacaoService {

    private final Logger log = LoggerFactory.getLogger(LocacaoServiceImpl.class);

    private LocacaoRepository locacaoRepository;

    private LocacaoMapper locacaoMapper;

    private LocacaoSearchRepository locacaoSearchRepository;

    public LocacaoServiceImpl(LocacaoRepository locacaoRepository, LocacaoMapper locacaoMapper, LocacaoSearchRepository locacaoSearchRepository) {
        this.locacaoRepository = locacaoRepository;
        this.locacaoMapper = locacaoMapper;
        this.locacaoSearchRepository = locacaoSearchRepository;
    }

    /**
     * Save a locacao.
     *
     * @param locacaoDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LocacaoDTO save(LocacaoDTO locacaoDTO) {
        log.debug("Request to save Locacao : {}", locacaoDTO);

        Locacao locacao = locacaoMapper.toEntity(locacaoDTO);
        locacao = locacaoRepository.save(locacao);
        LocacaoDTO result = locacaoMapper.toDto(locacao);
        locacaoSearchRepository.save(locacao);
        return result;
    }

    /**
     * Get all the locacaos.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LocacaoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Locacaos");
        return locacaoRepository.findAll(pageable)
            .map(locacaoMapper::toDto);
    }


    /**
     * Get one locacao by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LocacaoDTO> findOne(Long id) {
        log.debug("Request to get Locacao : {}", id);
        return locacaoRepository.findById(id)
            .map(locacaoMapper::toDto);
    }

    /**
     * Delete the locacao by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Locacao : {}", id);
        locacaoRepository.deleteById(id);
        locacaoSearchRepository.deleteById(id);
    }

    /**
     * Search for the locacao corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<LocacaoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Locacaos for query {}", query);
        return locacaoSearchRepository.search(queryStringQuery(query), pageable)
            .map(locacaoMapper::toDto);
    }
}
