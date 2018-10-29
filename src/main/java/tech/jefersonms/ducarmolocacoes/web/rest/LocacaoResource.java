package tech.jefersonms.ducarmolocacoes.web.rest;

import com.codahale.metrics.annotation.Timed;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.service.LocacaoService;
import tech.jefersonms.ducarmolocacoes.web.rest.errors.BadRequestAlertException;
import tech.jefersonms.ducarmolocacoes.web.rest.util.HeaderUtil;
import tech.jefersonms.ducarmolocacoes.web.rest.util.PaginationUtil;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Locacao.
 */
@RestController
@RequestMapping("/api")
public class LocacaoResource {

    private final Logger log = LoggerFactory.getLogger(LocacaoResource.class);

    private static final String ENTITY_NAME = "locacao";

    private LocacaoService locacaoService;

    public LocacaoResource(LocacaoService locacaoService) {
        this.locacaoService = locacaoService;
    }

    /**
     * POST  /locacaos : Create a new locacao.
     *
     * @param locacaoDTO the locacaoDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new locacaoDTO, or with status 400 (Bad Request) if the locacao has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/locacaos")
    @Timed
    public ResponseEntity<LocacaoDTO> createLocacao(@RequestBody LocacaoDTO locacaoDTO) throws URISyntaxException {
        log.debug("REST request to save Locacao : {}", locacaoDTO);
        if (locacaoDTO.getId() != null) {
            throw new BadRequestAlertException("A new locacao cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LocacaoDTO result = locacaoService.save(locacaoDTO);
        return ResponseEntity.created(new URI("/api/locacaos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /locacaos : Updates an existing locacao.
     *
     * @param locacaoDTO the locacaoDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated locacaoDTO,
     * or with status 400 (Bad Request) if the locacaoDTO is not valid,
     * or with status 500 (Internal Server Error) if the locacaoDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/locacaos")
    @Timed
    public ResponseEntity<LocacaoDTO> updateLocacao(@RequestBody LocacaoDTO locacaoDTO) throws URISyntaxException {
        log.debug("REST request to update Locacao : {}", locacaoDTO);
        if (locacaoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LocacaoDTO result = locacaoService.save(locacaoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, locacaoDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /locacaos : get all the locacaos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of locacaos in body
     */
    @GetMapping("/locacaos")
    @Timed
    public ResponseEntity<List<LocacaoDTO>> getAllLocacaos(Pageable pageable) {
        log.debug("REST request to get a page of Locacaos");
        Page<LocacaoDTO> page = locacaoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/locacaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /locacaos/:id : get the "id" locacao.
     *
     * @param id the id of the locacaoDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the locacaoDTO, or with status 404 (Not Found)
     */
    @GetMapping("/locacaos/{id}")
    @Timed
    public ResponseEntity<LocacaoDTO> getLocacao(@PathVariable Long id) {
        log.debug("REST request to get Locacao : {}", id);
        Optional<LocacaoDTO> locacaoDTO = locacaoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locacaoDTO);
    }

    /**
     * DELETE  /locacaos/:id : delete the "id" locacao.
     *
     * @param id the id of the locacaoDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/locacaos/{id}")
    @Timed
    public ResponseEntity<Void> deleteLocacao(@PathVariable Long id) {
        log.debug("REST request to delete Locacao : {}", id);
        locacaoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/locacaos?query=:query : search for the locacao corresponding
     * to the query.
     *
     * @param query the query of the locacao search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/locacaos")
    @Timed
    public ResponseEntity<List<LocacaoDTO>> searchLocacaos(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Locacaos for query {}", query);
        Page<LocacaoDTO> page = locacaoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/locacaos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
