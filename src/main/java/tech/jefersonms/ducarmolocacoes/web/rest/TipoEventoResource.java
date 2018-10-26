package tech.jefersonms.ducarmolocacoes.web.rest;

import com.codahale.metrics.annotation.Timed;
import tech.jefersonms.ducarmolocacoes.service.TipoEventoService;
import tech.jefersonms.ducarmolocacoes.web.rest.errors.BadRequestAlertException;
import tech.jefersonms.ducarmolocacoes.web.rest.util.HeaderUtil;
import tech.jefersonms.ducarmolocacoes.web.rest.util.PaginationUtil;
import tech.jefersonms.ducarmolocacoes.service.dto.TipoEventoDTO;
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
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing TipoEvento.
 */
@RestController
@RequestMapping("/api")
public class TipoEventoResource {

    private final Logger log = LoggerFactory.getLogger(TipoEventoResource.class);

    private static final String ENTITY_NAME = "tipoEvento";

    private TipoEventoService tipoEventoService;

    public TipoEventoResource(TipoEventoService tipoEventoService) {
        this.tipoEventoService = tipoEventoService;
    }

    /**
     * POST  /tipo-eventos : Create a new tipoEvento.
     *
     * @param tipoEventoDTO the tipoEventoDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tipoEventoDTO, or with status 400 (Bad Request) if the tipoEvento has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tipo-eventos")
    @Timed
    public ResponseEntity<TipoEventoDTO> createTipoEvento(@RequestBody TipoEventoDTO tipoEventoDTO) throws URISyntaxException {
        log.debug("REST request to save TipoEvento : {}", tipoEventoDTO);
        if (tipoEventoDTO.getId() != null) {
            throw new BadRequestAlertException("A new tipoEvento cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TipoEventoDTO result = tipoEventoService.save(tipoEventoDTO);
        return ResponseEntity.created(new URI("/api/tipo-eventos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tipo-eventos : Updates an existing tipoEvento.
     *
     * @param tipoEventoDTO the tipoEventoDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tipoEventoDTO,
     * or with status 400 (Bad Request) if the tipoEventoDTO is not valid,
     * or with status 500 (Internal Server Error) if the tipoEventoDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tipo-eventos")
    @Timed
    public ResponseEntity<TipoEventoDTO> updateTipoEvento(@RequestBody TipoEventoDTO tipoEventoDTO) throws URISyntaxException {
        log.debug("REST request to update TipoEvento : {}", tipoEventoDTO);
        if (tipoEventoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TipoEventoDTO result = tipoEventoService.save(tipoEventoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tipoEventoDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tipo-eventos : get all the tipoEventos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of tipoEventos in body
     */
    @GetMapping("/tipo-eventos")
    @Timed
    public ResponseEntity<List<TipoEventoDTO>> getAllTipoEventos(Pageable pageable) {
        log.debug("REST request to get a page of TipoEventos");
        Page<TipoEventoDTO> page = tipoEventoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipo-eventos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /tipo-eventos/:id : get the "id" tipoEvento.
     *
     * @param id the id of the tipoEventoDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tipoEventoDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tipo-eventos/{id}")
    @Timed
    public ResponseEntity<TipoEventoDTO> getTipoEvento(@PathVariable Long id) {
        log.debug("REST request to get TipoEvento : {}", id);
        Optional<TipoEventoDTO> tipoEventoDTO = tipoEventoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tipoEventoDTO);
    }

    /**
     * DELETE  /tipo-eventos/:id : delete the "id" tipoEvento.
     *
     * @param id the id of the tipoEventoDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tipo-eventos/{id}")
    @Timed
    public ResponseEntity<Void> deleteTipoEvento(@PathVariable Long id) {
        log.debug("REST request to delete TipoEvento : {}", id);
        tipoEventoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tipo-eventos?query=:query : search for the tipoEvento corresponding
     * to the query.
     *
     * @param query the query of the tipoEvento search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/tipo-eventos")
    @Timed
    public ResponseEntity<List<TipoEventoDTO>> searchTipoEventos(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of TipoEventos for query {}", query);
        Page<TipoEventoDTO> page = tipoEventoService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/tipo-eventos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
