package tech.jefersonms.ducarmolocacoes.web.rest;

import tech.jefersonms.ducarmolocacoes.DucarmolocacoesApp;

import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoProdutoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoProdutoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.LocacaoProdutoService;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoProdutoMapper;
import tech.jefersonms.ducarmolocacoes.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


import static tech.jefersonms.ducarmolocacoes.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LocacaoProdutoResource REST controller.
 *
 * @see LocacaoProdutoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DucarmolocacoesApp.class)
public class LocacaoProdutoResourceIntTest {

    private static final Integer DEFAULT_QUANTIDADE = 1;
    private static final Integer UPDATED_QUANTIDADE = 2;

    private static final BigDecimal DEFAULT_VALOR_UNITARIO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_UNITARIO = new BigDecimal(2);

    private static final BigDecimal DEFAULT_VALOR_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_TOTAL = new BigDecimal(2);

    @Autowired
    private LocacaoProdutoRepository locacaoProdutoRepository;

    @Autowired
    private LocacaoProdutoMapper locacaoProdutoMapper;
    
    @Autowired
    private LocacaoProdutoService locacaoProdutoService;

    /**
     * This repository is mocked in the tech.jefersonms.ducarmolocacoes.repository.search test package.
     *
     * @see tech.jefersonms.ducarmolocacoes.repository.search.LocacaoProdutoSearchRepositoryMockConfiguration
     */
    @Autowired
    private LocacaoProdutoSearchRepository mockLocacaoProdutoSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLocacaoProdutoMockMvc;

    private LocacaoProduto locacaoProduto;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LocacaoProdutoResource locacaoProdutoResource = new LocacaoProdutoResource(locacaoProdutoService);
        this.restLocacaoProdutoMockMvc = MockMvcBuilders.standaloneSetup(locacaoProdutoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LocacaoProduto createEntity(EntityManager em) {
        LocacaoProduto locacaoProduto = new LocacaoProduto()
            .quantidade(DEFAULT_QUANTIDADE)
            .valorUnitario(DEFAULT_VALOR_UNITARIO)
            .valorTotal(DEFAULT_VALOR_TOTAL);
        return locacaoProduto;
    }

    @Before
    public void initTest() {
        locacaoProduto = createEntity(em);
    }

    @Test
    @Transactional
    public void createLocacaoProduto() throws Exception {
        int databaseSizeBeforeCreate = locacaoProdutoRepository.findAll().size();

        // Create the LocacaoProduto
        LocacaoProdutoDTO locacaoProdutoDTO = locacaoProdutoMapper.toDto(locacaoProduto);
        restLocacaoProdutoMockMvc.perform(post("/api/locacao-produtos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoProdutoDTO)))
            .andExpect(status().isCreated());

        // Validate the LocacaoProduto in the database
        List<LocacaoProduto> locacaoProdutoList = locacaoProdutoRepository.findAll();
        assertThat(locacaoProdutoList).hasSize(databaseSizeBeforeCreate + 1);
        LocacaoProduto testLocacaoProduto = locacaoProdutoList.get(locacaoProdutoList.size() - 1);
        assertThat(testLocacaoProduto.getQuantidade()).isEqualTo(DEFAULT_QUANTIDADE);
        assertThat(testLocacaoProduto.getValorUnitario()).isEqualTo(DEFAULT_VALOR_UNITARIO);
        assertThat(testLocacaoProduto.getValorTotal()).isEqualTo(DEFAULT_VALOR_TOTAL);

        // Validate the LocacaoProduto in Elasticsearch
        verify(mockLocacaoProdutoSearchRepository, times(1)).save(testLocacaoProduto);
    }

    @Test
    @Transactional
    public void createLocacaoProdutoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = locacaoProdutoRepository.findAll().size();

        // Create the LocacaoProduto with an existing ID
        locacaoProduto.setId(1L);
        LocacaoProdutoDTO locacaoProdutoDTO = locacaoProdutoMapper.toDto(locacaoProduto);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocacaoProdutoMockMvc.perform(post("/api/locacao-produtos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoProdutoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LocacaoProduto in the database
        List<LocacaoProduto> locacaoProdutoList = locacaoProdutoRepository.findAll();
        assertThat(locacaoProdutoList).hasSize(databaseSizeBeforeCreate);

        // Validate the LocacaoProduto in Elasticsearch
        verify(mockLocacaoProdutoSearchRepository, times(0)).save(locacaoProduto);
    }

    @Test
    @Transactional
    public void getAllLocacaoProdutos() throws Exception {
        // Initialize the database
        locacaoProdutoRepository.saveAndFlush(locacaoProduto);

        // Get all the locacaoProdutoList
        restLocacaoProdutoMockMvc.perform(get("/api/locacao-produtos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locacaoProduto.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].valorUnitario").value(hasItem(DEFAULT_VALOR_UNITARIO.intValue())))
            .andExpect(jsonPath("$.[*].valorTotal").value(hasItem(DEFAULT_VALOR_TOTAL.intValue())));
    }
    
    @Test
    @Transactional
    public void getLocacaoProduto() throws Exception {
        // Initialize the database
        locacaoProdutoRepository.saveAndFlush(locacaoProduto);

        // Get the locacaoProduto
        restLocacaoProdutoMockMvc.perform(get("/api/locacao-produtos/{id}", locacaoProduto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(locacaoProduto.getId().intValue()))
            .andExpect(jsonPath("$.quantidade").value(DEFAULT_QUANTIDADE))
            .andExpect(jsonPath("$.valorUnitario").value(DEFAULT_VALOR_UNITARIO.intValue()))
            .andExpect(jsonPath("$.valorTotal").value(DEFAULT_VALOR_TOTAL.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingLocacaoProduto() throws Exception {
        // Get the locacaoProduto
        restLocacaoProdutoMockMvc.perform(get("/api/locacao-produtos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocacaoProduto() throws Exception {
        // Initialize the database
        locacaoProdutoRepository.saveAndFlush(locacaoProduto);

        int databaseSizeBeforeUpdate = locacaoProdutoRepository.findAll().size();

        // Update the locacaoProduto
        LocacaoProduto updatedLocacaoProduto = locacaoProdutoRepository.findById(locacaoProduto.getId()).get();
        // Disconnect from session so that the updates on updatedLocacaoProduto are not directly saved in db
        em.detach(updatedLocacaoProduto);
        updatedLocacaoProduto
            .quantidade(UPDATED_QUANTIDADE)
            .valorUnitario(UPDATED_VALOR_UNITARIO)
            .valorTotal(UPDATED_VALOR_TOTAL);
        LocacaoProdutoDTO locacaoProdutoDTO = locacaoProdutoMapper.toDto(updatedLocacaoProduto);

        restLocacaoProdutoMockMvc.perform(put("/api/locacao-produtos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoProdutoDTO)))
            .andExpect(status().isOk());

        // Validate the LocacaoProduto in the database
        List<LocacaoProduto> locacaoProdutoList = locacaoProdutoRepository.findAll();
        assertThat(locacaoProdutoList).hasSize(databaseSizeBeforeUpdate);
        LocacaoProduto testLocacaoProduto = locacaoProdutoList.get(locacaoProdutoList.size() - 1);
        assertThat(testLocacaoProduto.getQuantidade()).isEqualTo(UPDATED_QUANTIDADE);
        assertThat(testLocacaoProduto.getValorUnitario()).isEqualTo(UPDATED_VALOR_UNITARIO);
        assertThat(testLocacaoProduto.getValorTotal()).isEqualTo(UPDATED_VALOR_TOTAL);

        // Validate the LocacaoProduto in Elasticsearch
        verify(mockLocacaoProdutoSearchRepository, times(1)).save(testLocacaoProduto);
    }

    @Test
    @Transactional
    public void updateNonExistingLocacaoProduto() throws Exception {
        int databaseSizeBeforeUpdate = locacaoProdutoRepository.findAll().size();

        // Create the LocacaoProduto
        LocacaoProdutoDTO locacaoProdutoDTO = locacaoProdutoMapper.toDto(locacaoProduto);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocacaoProdutoMockMvc.perform(put("/api/locacao-produtos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoProdutoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the LocacaoProduto in the database
        List<LocacaoProduto> locacaoProdutoList = locacaoProdutoRepository.findAll();
        assertThat(locacaoProdutoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the LocacaoProduto in Elasticsearch
        verify(mockLocacaoProdutoSearchRepository, times(0)).save(locacaoProduto);
    }

    @Test
    @Transactional
    public void deleteLocacaoProduto() throws Exception {
        // Initialize the database
        locacaoProdutoRepository.saveAndFlush(locacaoProduto);

        int databaseSizeBeforeDelete = locacaoProdutoRepository.findAll().size();

        // Get the locacaoProduto
        restLocacaoProdutoMockMvc.perform(delete("/api/locacao-produtos/{id}", locacaoProduto.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LocacaoProduto> locacaoProdutoList = locacaoProdutoRepository.findAll();
        assertThat(locacaoProdutoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the LocacaoProduto in Elasticsearch
        verify(mockLocacaoProdutoSearchRepository, times(1)).deleteById(locacaoProduto.getId());
    }

    @Test
    @Transactional
    public void searchLocacaoProduto() throws Exception {
        // Initialize the database
        locacaoProdutoRepository.saveAndFlush(locacaoProduto);
        when(mockLocacaoProdutoSearchRepository.search(queryStringQuery("id:" + locacaoProduto.getId())))
            .thenReturn(Collections.singletonList(locacaoProduto));
        // Search the locacaoProduto
        restLocacaoProdutoMockMvc.perform(get("/api/_search/locacao-produtos?query=id:" + locacaoProduto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locacaoProduto.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantidade").value(hasItem(DEFAULT_QUANTIDADE)))
            .andExpect(jsonPath("$.[*].valorUnitario").value(hasItem(DEFAULT_VALOR_UNITARIO.intValue())))
            .andExpect(jsonPath("$.[*].valorTotal").value(hasItem(DEFAULT_VALOR_TOTAL.intValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocacaoProduto.class);
        LocacaoProduto locacaoProduto1 = new LocacaoProduto();
        locacaoProduto1.setId(1L);
        LocacaoProduto locacaoProduto2 = new LocacaoProduto();
        locacaoProduto2.setId(locacaoProduto1.getId());
        assertThat(locacaoProduto1).isEqualTo(locacaoProduto2);
        locacaoProduto2.setId(2L);
        assertThat(locacaoProduto1).isNotEqualTo(locacaoProduto2);
        locacaoProduto1.setId(null);
        assertThat(locacaoProduto1).isNotEqualTo(locacaoProduto2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocacaoProdutoDTO.class);
        LocacaoProdutoDTO locacaoProdutoDTO1 = new LocacaoProdutoDTO();
        locacaoProdutoDTO1.setId(1L);
        LocacaoProdutoDTO locacaoProdutoDTO2 = new LocacaoProdutoDTO();
        assertThat(locacaoProdutoDTO1).isNotEqualTo(locacaoProdutoDTO2);
        locacaoProdutoDTO2.setId(locacaoProdutoDTO1.getId());
        assertThat(locacaoProdutoDTO1).isEqualTo(locacaoProdutoDTO2);
        locacaoProdutoDTO2.setId(2L);
        assertThat(locacaoProdutoDTO1).isNotEqualTo(locacaoProdutoDTO2);
        locacaoProdutoDTO1.setId(null);
        assertThat(locacaoProdutoDTO1).isNotEqualTo(locacaoProdutoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(locacaoProdutoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(locacaoProdutoMapper.fromId(null)).isNull();
    }
}
