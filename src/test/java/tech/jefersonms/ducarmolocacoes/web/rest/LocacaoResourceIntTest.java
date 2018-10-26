package tech.jefersonms.ducarmolocacoes.web.rest;

import tech.jefersonms.ducarmolocacoes.DucarmolocacoesApp;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.LocacaoService;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoMapper;
import tech.jefersonms.ducarmolocacoes.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


import static tech.jefersonms.ducarmolocacoes.web.rest.TestUtil.sameInstant;
import static tech.jefersonms.ducarmolocacoes.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LocacaoResource REST controller.
 *
 * @see LocacaoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DucarmolocacoesApp.class)
public class LocacaoResourceIntTest {

    private static final BigDecimal DEFAULT_VALOR_TOTAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_TOTAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_VALOR_SINAL = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_SINAL = new BigDecimal(2);

    private static final BigDecimal DEFAULT_VALOR_ADIANTADO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_ADIANTADO = new BigDecimal(2);

    private static final BigDecimal DEFAULT_VALOR_SALDO = new BigDecimal(1);
    private static final BigDecimal UPDATED_VALOR_SALDO = new BigDecimal(2);

    private static final ZonedDateTime DEFAULT_DATA_CONTRATACAO = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_CONTRATACAO = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final LocalDate DEFAULT_DATA_EVENTO = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_EVENTO = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATA_DEV_PREV = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_DEV_PREV = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_DATA_DEV_REAL = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_DEV_REAL = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final LocalDate DEFAULT_DATA_ENTR_PREV = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_ENTR_PREV = LocalDate.now(ZoneId.systemDefault());

    private static final ZonedDateTime DEFAULT_DATA_ENTR_REAL = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATA_ENTR_REAL = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final Integer DEFAULT_SITUACAO = 1;
    private static final Integer UPDATED_SITUACAO = 2;

    private static final String DEFAULT_HTML_CONTRATO = "AAAAAAAAAA";
    private static final String UPDATED_HTML_CONTRATO = "BBBBBBBBBB";

    @Autowired
    private LocacaoRepository locacaoRepository;

    @Autowired
    private LocacaoMapper locacaoMapper;
    
    @Autowired
    private LocacaoService locacaoService;

    /**
     * This repository is mocked in the tech.jefersonms.ducarmolocacoes.repository.search test package.
     *
     * @see tech.jefersonms.ducarmolocacoes.repository.search.LocacaoSearchRepositoryMockConfiguration
     */
    @Autowired
    private LocacaoSearchRepository mockLocacaoSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLocacaoMockMvc;

    private Locacao locacao;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final LocacaoResource locacaoResource = new LocacaoResource(locacaoService);
        this.restLocacaoMockMvc = MockMvcBuilders.standaloneSetup(locacaoResource)
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
    public static Locacao createEntity(EntityManager em) {
        Locacao locacao = new Locacao()
            .valorTotal(DEFAULT_VALOR_TOTAL)
            .valorSinal(DEFAULT_VALOR_SINAL)
            .valorAdiantado(DEFAULT_VALOR_ADIANTADO)
            .valorSaldo(DEFAULT_VALOR_SALDO)
            .dataContratacao(DEFAULT_DATA_CONTRATACAO)
            .dataEvento(DEFAULT_DATA_EVENTO)
            .dataDevPrev(DEFAULT_DATA_DEV_PREV)
            .dataDevReal(DEFAULT_DATA_DEV_REAL)
            .dataEntrPrev(DEFAULT_DATA_ENTR_PREV)
            .dataEntrReal(DEFAULT_DATA_ENTR_REAL)
            .situacao(DEFAULT_SITUACAO)
            .htmlContrato(DEFAULT_HTML_CONTRATO);
        return locacao;
    }

    @Before
    public void initTest() {
        locacao = createEntity(em);
    }

    @Test
    @Transactional
    public void createLocacao() throws Exception {
        int databaseSizeBeforeCreate = locacaoRepository.findAll().size();

        // Create the Locacao
        LocacaoDTO locacaoDTO = locacaoMapper.toDto(locacao);
        restLocacaoMockMvc.perform(post("/api/locacaos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoDTO)))
            .andExpect(status().isCreated());

        // Validate the Locacao in the database
        List<Locacao> locacaoList = locacaoRepository.findAll();
        assertThat(locacaoList).hasSize(databaseSizeBeforeCreate + 1);
        Locacao testLocacao = locacaoList.get(locacaoList.size() - 1);
        assertThat(testLocacao.getValorTotal()).isEqualTo(DEFAULT_VALOR_TOTAL);
        assertThat(testLocacao.getValorSinal()).isEqualTo(DEFAULT_VALOR_SINAL);
        assertThat(testLocacao.getValorAdiantado()).isEqualTo(DEFAULT_VALOR_ADIANTADO);
        assertThat(testLocacao.getValorSaldo()).isEqualTo(DEFAULT_VALOR_SALDO);
        assertThat(testLocacao.getDataContratacao()).isEqualTo(DEFAULT_DATA_CONTRATACAO);
        assertThat(testLocacao.getDataEvento()).isEqualTo(DEFAULT_DATA_EVENTO);
        assertThat(testLocacao.getDataDevPrev()).isEqualTo(DEFAULT_DATA_DEV_PREV);
        assertThat(testLocacao.getDataDevReal()).isEqualTo(DEFAULT_DATA_DEV_REAL);
        assertThat(testLocacao.getDataEntrPrev()).isEqualTo(DEFAULT_DATA_ENTR_PREV);
        assertThat(testLocacao.getDataEntrReal()).isEqualTo(DEFAULT_DATA_ENTR_REAL);
        assertThat(testLocacao.getSituacao()).isEqualTo(DEFAULT_SITUACAO);
        assertThat(testLocacao.getHtmlContrato()).isEqualTo(DEFAULT_HTML_CONTRATO);

        // Validate the Locacao in Elasticsearch
        verify(mockLocacaoSearchRepository, times(1)).save(testLocacao);
    }

    @Test
    @Transactional
    public void createLocacaoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = locacaoRepository.findAll().size();

        // Create the Locacao with an existing ID
        locacao.setId(1L);
        LocacaoDTO locacaoDTO = locacaoMapper.toDto(locacao);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLocacaoMockMvc.perform(post("/api/locacaos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Locacao in the database
        List<Locacao> locacaoList = locacaoRepository.findAll();
        assertThat(locacaoList).hasSize(databaseSizeBeforeCreate);

        // Validate the Locacao in Elasticsearch
        verify(mockLocacaoSearchRepository, times(0)).save(locacao);
    }

    @Test
    @Transactional
    public void getAllLocacaos() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        // Get all the locacaoList
        restLocacaoMockMvc.perform(get("/api/locacaos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].valorTotal").value(hasItem(DEFAULT_VALOR_TOTAL.intValue())))
            .andExpect(jsonPath("$.[*].valorSinal").value(hasItem(DEFAULT_VALOR_SINAL.intValue())))
            .andExpect(jsonPath("$.[*].valorAdiantado").value(hasItem(DEFAULT_VALOR_ADIANTADO.intValue())))
            .andExpect(jsonPath("$.[*].valorSaldo").value(hasItem(DEFAULT_VALOR_SALDO.intValue())))
            .andExpect(jsonPath("$.[*].dataContratacao").value(hasItem(sameInstant(DEFAULT_DATA_CONTRATACAO))))
            .andExpect(jsonPath("$.[*].dataEvento").value(hasItem(DEFAULT_DATA_EVENTO.toString())))
            .andExpect(jsonPath("$.[*].dataDevPrev").value(hasItem(DEFAULT_DATA_DEV_PREV.toString())))
            .andExpect(jsonPath("$.[*].dataDevReal").value(hasItem(sameInstant(DEFAULT_DATA_DEV_REAL))))
            .andExpect(jsonPath("$.[*].dataEntrPrev").value(hasItem(DEFAULT_DATA_ENTR_PREV.toString())))
            .andExpect(jsonPath("$.[*].dataEntrReal").value(hasItem(sameInstant(DEFAULT_DATA_ENTR_REAL))))
            .andExpect(jsonPath("$.[*].situacao").value(hasItem(DEFAULT_SITUACAO)))
            .andExpect(jsonPath("$.[*].htmlContrato").value(hasItem(DEFAULT_HTML_CONTRATO.toString())));
    }
    
    @Test
    @Transactional
    public void getLocacao() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        // Get the locacao
        restLocacaoMockMvc.perform(get("/api/locacaos/{id}", locacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(locacao.getId().intValue()))
            .andExpect(jsonPath("$.valorTotal").value(DEFAULT_VALOR_TOTAL.intValue()))
            .andExpect(jsonPath("$.valorSinal").value(DEFAULT_VALOR_SINAL.intValue()))
            .andExpect(jsonPath("$.valorAdiantado").value(DEFAULT_VALOR_ADIANTADO.intValue()))
            .andExpect(jsonPath("$.valorSaldo").value(DEFAULT_VALOR_SALDO.intValue()))
            .andExpect(jsonPath("$.dataContratacao").value(sameInstant(DEFAULT_DATA_CONTRATACAO)))
            .andExpect(jsonPath("$.dataEvento").value(DEFAULT_DATA_EVENTO.toString()))
            .andExpect(jsonPath("$.dataDevPrev").value(DEFAULT_DATA_DEV_PREV.toString()))
            .andExpect(jsonPath("$.dataDevReal").value(sameInstant(DEFAULT_DATA_DEV_REAL)))
            .andExpect(jsonPath("$.dataEntrPrev").value(DEFAULT_DATA_ENTR_PREV.toString()))
            .andExpect(jsonPath("$.dataEntrReal").value(sameInstant(DEFAULT_DATA_ENTR_REAL)))
            .andExpect(jsonPath("$.situacao").value(DEFAULT_SITUACAO))
            .andExpect(jsonPath("$.htmlContrato").value(DEFAULT_HTML_CONTRATO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingLocacao() throws Exception {
        // Get the locacao
        restLocacaoMockMvc.perform(get("/api/locacaos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLocacao() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        int databaseSizeBeforeUpdate = locacaoRepository.findAll().size();

        // Update the locacao
        Locacao updatedLocacao = locacaoRepository.findById(locacao.getId()).get();
        // Disconnect from session so that the updates on updatedLocacao are not directly saved in db
        em.detach(updatedLocacao);
        updatedLocacao
            .valorTotal(UPDATED_VALOR_TOTAL)
            .valorSinal(UPDATED_VALOR_SINAL)
            .valorAdiantado(UPDATED_VALOR_ADIANTADO)
            .valorSaldo(UPDATED_VALOR_SALDO)
            .dataContratacao(UPDATED_DATA_CONTRATACAO)
            .dataEvento(UPDATED_DATA_EVENTO)
            .dataDevPrev(UPDATED_DATA_DEV_PREV)
            .dataDevReal(UPDATED_DATA_DEV_REAL)
            .dataEntrPrev(UPDATED_DATA_ENTR_PREV)
            .dataEntrReal(UPDATED_DATA_ENTR_REAL)
            .situacao(UPDATED_SITUACAO)
            .htmlContrato(UPDATED_HTML_CONTRATO);
        LocacaoDTO locacaoDTO = locacaoMapper.toDto(updatedLocacao);

        restLocacaoMockMvc.perform(put("/api/locacaos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoDTO)))
            .andExpect(status().isOk());

        // Validate the Locacao in the database
        List<Locacao> locacaoList = locacaoRepository.findAll();
        assertThat(locacaoList).hasSize(databaseSizeBeforeUpdate);
        Locacao testLocacao = locacaoList.get(locacaoList.size() - 1);
        assertThat(testLocacao.getValorTotal()).isEqualTo(UPDATED_VALOR_TOTAL);
        assertThat(testLocacao.getValorSinal()).isEqualTo(UPDATED_VALOR_SINAL);
        assertThat(testLocacao.getValorAdiantado()).isEqualTo(UPDATED_VALOR_ADIANTADO);
        assertThat(testLocacao.getValorSaldo()).isEqualTo(UPDATED_VALOR_SALDO);
        assertThat(testLocacao.getDataContratacao()).isEqualTo(UPDATED_DATA_CONTRATACAO);
        assertThat(testLocacao.getDataEvento()).isEqualTo(UPDATED_DATA_EVENTO);
        assertThat(testLocacao.getDataDevPrev()).isEqualTo(UPDATED_DATA_DEV_PREV);
        assertThat(testLocacao.getDataDevReal()).isEqualTo(UPDATED_DATA_DEV_REAL);
        assertThat(testLocacao.getDataEntrPrev()).isEqualTo(UPDATED_DATA_ENTR_PREV);
        assertThat(testLocacao.getDataEntrReal()).isEqualTo(UPDATED_DATA_ENTR_REAL);
        assertThat(testLocacao.getSituacao()).isEqualTo(UPDATED_SITUACAO);
        assertThat(testLocacao.getHtmlContrato()).isEqualTo(UPDATED_HTML_CONTRATO);

        // Validate the Locacao in Elasticsearch
        verify(mockLocacaoSearchRepository, times(1)).save(testLocacao);
    }

    @Test
    @Transactional
    public void updateNonExistingLocacao() throws Exception {
        int databaseSizeBeforeUpdate = locacaoRepository.findAll().size();

        // Create the Locacao
        LocacaoDTO locacaoDTO = locacaoMapper.toDto(locacao);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLocacaoMockMvc.perform(put("/api/locacaos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(locacaoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Locacao in the database
        List<Locacao> locacaoList = locacaoRepository.findAll();
        assertThat(locacaoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Locacao in Elasticsearch
        verify(mockLocacaoSearchRepository, times(0)).save(locacao);
    }

    @Test
    @Transactional
    public void deleteLocacao() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);

        int databaseSizeBeforeDelete = locacaoRepository.findAll().size();

        // Get the locacao
        restLocacaoMockMvc.perform(delete("/api/locacaos/{id}", locacao.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Locacao> locacaoList = locacaoRepository.findAll();
        assertThat(locacaoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Locacao in Elasticsearch
        verify(mockLocacaoSearchRepository, times(1)).deleteById(locacao.getId());
    }

    @Test
    @Transactional
    public void searchLocacao() throws Exception {
        // Initialize the database
        locacaoRepository.saveAndFlush(locacao);
        when(mockLocacaoSearchRepository.search(queryStringQuery("id:" + locacao.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(locacao), PageRequest.of(0, 1), 1));
        // Search the locacao
        restLocacaoMockMvc.perform(get("/api/_search/locacaos?query=id:" + locacao.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(locacao.getId().intValue())))
            .andExpect(jsonPath("$.[*].valorTotal").value(hasItem(DEFAULT_VALOR_TOTAL.intValue())))
            .andExpect(jsonPath("$.[*].valorSinal").value(hasItem(DEFAULT_VALOR_SINAL.intValue())))
            .andExpect(jsonPath("$.[*].valorAdiantado").value(hasItem(DEFAULT_VALOR_ADIANTADO.intValue())))
            .andExpect(jsonPath("$.[*].valorSaldo").value(hasItem(DEFAULT_VALOR_SALDO.intValue())))
            .andExpect(jsonPath("$.[*].dataContratacao").value(hasItem(sameInstant(DEFAULT_DATA_CONTRATACAO))))
            .andExpect(jsonPath("$.[*].dataEvento").value(hasItem(DEFAULT_DATA_EVENTO.toString())))
            .andExpect(jsonPath("$.[*].dataDevPrev").value(hasItem(DEFAULT_DATA_DEV_PREV.toString())))
            .andExpect(jsonPath("$.[*].dataDevReal").value(hasItem(sameInstant(DEFAULT_DATA_DEV_REAL))))
            .andExpect(jsonPath("$.[*].dataEntrPrev").value(hasItem(DEFAULT_DATA_ENTR_PREV.toString())))
            .andExpect(jsonPath("$.[*].dataEntrReal").value(hasItem(sameInstant(DEFAULT_DATA_ENTR_REAL))))
            .andExpect(jsonPath("$.[*].situacao").value(hasItem(DEFAULT_SITUACAO)))
            .andExpect(jsonPath("$.[*].htmlContrato").value(hasItem(DEFAULT_HTML_CONTRATO.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Locacao.class);
        Locacao locacao1 = new Locacao();
        locacao1.setId(1L);
        Locacao locacao2 = new Locacao();
        locacao2.setId(locacao1.getId());
        assertThat(locacao1).isEqualTo(locacao2);
        locacao2.setId(2L);
        assertThat(locacao1).isNotEqualTo(locacao2);
        locacao1.setId(null);
        assertThat(locacao1).isNotEqualTo(locacao2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LocacaoDTO.class);
        LocacaoDTO locacaoDTO1 = new LocacaoDTO();
        locacaoDTO1.setId(1L);
        LocacaoDTO locacaoDTO2 = new LocacaoDTO();
        assertThat(locacaoDTO1).isNotEqualTo(locacaoDTO2);
        locacaoDTO2.setId(locacaoDTO1.getId());
        assertThat(locacaoDTO1).isEqualTo(locacaoDTO2);
        locacaoDTO2.setId(2L);
        assertThat(locacaoDTO1).isNotEqualTo(locacaoDTO2);
        locacaoDTO1.setId(null);
        assertThat(locacaoDTO1).isNotEqualTo(locacaoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(locacaoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(locacaoMapper.fromId(null)).isNull();
    }
}
