package tech.jefersonms.ducarmolocacoes.service.impl;

import tech.jefersonms.ducarmolocacoes.service.ClienteService;
import tech.jefersonms.ducarmolocacoes.domain.Cliente;
import tech.jefersonms.ducarmolocacoes.repository.ClienteRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.ClienteSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.dto.ClienteDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.ClienteMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Cliente.
 */
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    private final Logger log = LoggerFactory.getLogger(ClienteServiceImpl.class);

    private ClienteRepository clienteRepository;

    private ClienteMapper clienteMapper;

    private ClienteSearchRepository clienteSearchRepository;

    public ClienteServiceImpl(ClienteRepository clienteRepository, ClienteMapper clienteMapper, ClienteSearchRepository clienteSearchRepository) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.clienteSearchRepository = clienteSearchRepository;
    }

    /**
     * Save a cliente.
     *
     * @param clienteDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ClienteDTO save(ClienteDTO clienteDTO) {
        log.debug("Request to save Cliente : {}", clienteDTO);

        Cliente cliente = clienteMapper.toEntity(clienteDTO);
        cliente = clienteRepository.save(cliente);
        ClienteDTO result = clienteMapper.toDto(cliente);
        clienteSearchRepository.save(cliente);
        return result;
    }

    /**
     * Get all the clientes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Clientes");
        return clienteRepository.findAll(pageable)
            .map(clienteMapper::toDto);
    }


    /**
     * Get one cliente by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDTO> findOne(Long id) {
        log.debug("Request to get Cliente : {}", id);
        return clienteRepository.findById(id)
            .map(clienteMapper::toDto);
    }

    /**
     * Delete the cliente by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Cliente : {}", id);
        clienteRepository.deleteById(id);
        clienteSearchRepository.deleteById(id);
    }

    /**
     * Search for the cliente corresponding to the query.
     *
     * @param query the query of the search
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Clientes for query {}", query);
        return clienteSearchRepository.search(queryStringQuery(query), pageable)
            .map(clienteMapper::toDto);
    }
}
