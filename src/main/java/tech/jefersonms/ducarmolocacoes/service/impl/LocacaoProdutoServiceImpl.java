package tech.jefersonms.ducarmolocacoes.service.impl;

import tech.jefersonms.ducarmolocacoes.service.LocacaoProdutoService;
import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoProdutoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoProdutoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoMapper;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoProdutoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing LocacaoProduto.
 */
@Service
@Transactional
public class LocacaoProdutoServiceImpl implements LocacaoProdutoService {

    private final Logger log = LoggerFactory.getLogger(LocacaoProdutoServiceImpl.class);

    private LocacaoProdutoRepository locacaoProdutoRepository;
    private LocacaoProdutoMapper locacaoProdutoMapper;
    private LocacaoProdutoSearchRepository locacaoProdutoSearchRepository;
    private LocacaoMapper locacaoMapper;

    public LocacaoProdutoServiceImpl(LocacaoProdutoRepository locacaoProdutoRepository,
                                     LocacaoProdutoMapper locacaoProdutoMapper,
                                     LocacaoProdutoSearchRepository locacaoProdutoSearchRepository,
                                     LocacaoMapper locacaoMapper) {
        this.locacaoProdutoRepository = locacaoProdutoRepository;
        this.locacaoProdutoMapper = locacaoProdutoMapper;
        this.locacaoProdutoSearchRepository = locacaoProdutoSearchRepository;
        this.locacaoMapper = locacaoMapper;
    }

    /**
     * Save a locacaoProduto.
     *
     * @param locacaoProdutoDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public LocacaoProdutoDTO save(LocacaoProdutoDTO locacaoProdutoDTO) {
        log.debug("Request to save LocacaoProduto : {}", locacaoProdutoDTO);

        LocacaoProduto locacaoProduto = locacaoProdutoMapper.toEntity(locacaoProdutoDTO);
        locacaoProduto = locacaoProdutoRepository.save(locacaoProduto);
        LocacaoProdutoDTO result = locacaoProdutoMapper.toDto(locacaoProduto);
        locacaoProdutoSearchRepository.save(locacaoProduto);
        return result;
    }

    @Override
    public List<LocacaoProdutoDTO> save(LocacaoDTO locacaoDTO, List<LocacaoProdutoDTO> locacaoProdutoDTO) {
        List<LocacaoProdutoDTO> result = new ArrayList<>();

        for (LocacaoProdutoDTO lpDTO : locacaoProdutoDTO) {
            lpDTO.setLocacao(locacaoDTO);
            lpDTO.setLocacaoId(locacaoDTO.getId());
            result.add(this.save(lpDTO));
        }

        return result;
    }

    /**
     * Get all the locacaoProdutos.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocacaoProdutoDTO> findAll() {
        log.debug("Request to get all LocacaoProdutos");
        return locacaoProdutoRepository.findAll().stream()
            .map(locacaoProdutoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<LocacaoProdutoDTO> findBy(LocacaoDTO locacao) {
        log.debug("Request to get all LocacaoProdutos of locacao id: {}", locacao);

        return locacaoProdutoRepository.findByLocacao(locacaoMapper.toEntity(locacao)).stream()
            .map(locacaoProdutoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }


    /**
     * Get one locacaoProduto by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<LocacaoProdutoDTO> findOne(Long id) {
        log.debug("Request to get LocacaoProduto : {}", id);
        return locacaoProdutoRepository.findById(id)
            .map(locacaoProdutoMapper::toDto);
    }

    /**
     * Delete the locacaoProduto by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete LocacaoProduto : {}", id);
        locacaoProdutoRepository.deleteById(id);
        locacaoProdutoSearchRepository.deleteById(id);
    }

    /**
     * Search for the locacaoProduto corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<LocacaoProdutoDTO> search(String query) {
        log.debug("Request to search LocacaoProdutos for query {}", query);
        return StreamSupport
            .stream(locacaoProdutoSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(locacaoProdutoMapper::toDto)
            .collect(Collectors.toList());
    }
}
