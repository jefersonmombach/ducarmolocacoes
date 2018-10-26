package tech.jefersonms.ducarmolocacoes.web.rest;

import com.codahale.metrics.annotation.Timed;
import tech.jefersonms.ducarmolocacoes.service.LocacaoProdutoService;
import tech.jefersonms.ducarmolocacoes.web.rest.errors.BadRequestAlertException;
import tech.jefersonms.ducarmolocacoes.web.rest.util.HeaderUtil;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing LocacaoProduto.
 */
@RestController
@RequestMapping("/api")
public class LocacaoProdutoResource {

    private final Logger log = LoggerFactory.getLogger(LocacaoProdutoResource.class);

    private static final String ENTITY_NAME = "locacaoProduto";

    private LocacaoProdutoService locacaoProdutoService;

    public LocacaoProdutoResource(LocacaoProdutoService locacaoProdutoService) {
        this.locacaoProdutoService = locacaoProdutoService;
    }

    /**
     * POST  /locacao-produtos : Create a new locacaoProduto.
     *
     * @param locacaoProdutoDTO the locacaoProdutoDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new locacaoProdutoDTO, or with status 400 (Bad Request) if the locacaoProduto has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/locacao-produtos")
    @Timed
    public ResponseEntity<LocacaoProdutoDTO> createLocacaoProduto(@RequestBody LocacaoProdutoDTO locacaoProdutoDTO) throws URISyntaxException {
        log.debug("REST request to save LocacaoProduto : {}", locacaoProdutoDTO);
        if (locacaoProdutoDTO.getId() != null) {
            throw new BadRequestAlertException("A new locacaoProduto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LocacaoProdutoDTO result = locacaoProdutoService.save(locacaoProdutoDTO);
        return ResponseEntity.created(new URI("/api/locacao-produtos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /locacao-produtos : Updates an existing locacaoProduto.
     *
     * @param locacaoProdutoDTO the locacaoProdutoDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated locacaoProdutoDTO,
     * or with status 400 (Bad Request) if the locacaoProdutoDTO is not valid,
     * or with status 500 (Internal Server Error) if the locacaoProdutoDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/locacao-produtos")
    @Timed
    public ResponseEntity<LocacaoProdutoDTO> updateLocacaoProduto(@RequestBody LocacaoProdutoDTO locacaoProdutoDTO) throws URISyntaxException {
        log.debug("REST request to update LocacaoProduto : {}", locacaoProdutoDTO);
        if (locacaoProdutoDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        LocacaoProdutoDTO result = locacaoProdutoService.save(locacaoProdutoDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, locacaoProdutoDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /locacao-produtos : get all the locacaoProdutos.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of locacaoProdutos in body
     */
    @GetMapping("/locacao-produtos")
    @Timed
    public List<LocacaoProdutoDTO> getAllLocacaoProdutos() {
        log.debug("REST request to get all LocacaoProdutos");
        return locacaoProdutoService.findAll();
    }

    /**
     * GET  /locacao-produtos/:id : get the "id" locacaoProduto.
     *
     * @param id the id of the locacaoProdutoDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the locacaoProdutoDTO, or with status 404 (Not Found)
     */
    @GetMapping("/locacao-produtos/{id}")
    @Timed
    public ResponseEntity<LocacaoProdutoDTO> getLocacaoProduto(@PathVariable Long id) {
        log.debug("REST request to get LocacaoProduto : {}", id);
        Optional<LocacaoProdutoDTO> locacaoProdutoDTO = locacaoProdutoService.findOne(id);
        return ResponseUtil.wrapOrNotFound(locacaoProdutoDTO);
    }

    /**
     * DELETE  /locacao-produtos/:id : delete the "id" locacaoProduto.
     *
     * @param id the id of the locacaoProdutoDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/locacao-produtos/{id}")
    @Timed
    public ResponseEntity<Void> deleteLocacaoProduto(@PathVariable Long id) {
        log.debug("REST request to delete LocacaoProduto : {}", id);
        locacaoProdutoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/locacao-produtos?query=:query : search for the locacaoProduto corresponding
     * to the query.
     *
     * @param query the query of the locacaoProduto search
     * @return the result of the search
     */
    @GetMapping("/_search/locacao-produtos")
    @Timed
    public List<LocacaoProdutoDTO> searchLocacaoProdutos(@RequestParam String query) {
        log.debug("REST request to search LocacaoProdutos for query {}", query);
        return locacaoProdutoService.search(query);
    }

}
