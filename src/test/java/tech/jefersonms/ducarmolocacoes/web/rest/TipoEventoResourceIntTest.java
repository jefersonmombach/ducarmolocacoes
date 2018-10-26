package tech.jefersonms.ducarmolocacoes.web.rest;

import tech.jefersonms.ducarmolocacoes.DucarmolocacoesApp;

import tech.jefersonms.ducarmolocacoes.domain.TipoEvento;
import tech.jefersonms.ducarmolocacoes.repository.TipoEventoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.TipoEventoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.TipoEventoService;
import tech.jefersonms.ducarmolocacoes.service.dto.TipoEventoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.TipoEventoMapper;
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
 * Test class for the TipoEventoResource REST controller.
 *
 * @see TipoEventoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DucarmolocacoesApp.class)
public class TipoEventoResourceIntTest {

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private TipoEventoMapper tipoEventoMapper;
    
    @Autowired
    private TipoEventoService tipoEventoService;

    /**
     * This repository is mocked in the tech.jefersonms.ducarmolocacoes.repository.search test package.
     *
     * @see tech.jefersonms.ducarmolocacoes.repository.search.TipoEventoSearchRepositoryMockConfiguration
     */
    @Autowired
    private TipoEventoSearchRepository mockTipoEventoSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTipoEventoMockMvc;

    private TipoEvento tipoEvento;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TipoEventoResource tipoEventoResource = new TipoEventoResource(tipoEventoService);
        this.restTipoEventoMockMvc = MockMvcBuilders.standaloneSetup(tipoEventoResource)
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
    public static TipoEvento createEntity(EntityManager em) {
        TipoEvento tipoEvento = new TipoEvento()
            .descricao(DEFAULT_DESCRICAO);
        return tipoEvento;
    }

    @Before
    public void initTest() {
        tipoEvento = createEntity(em);
    }

    @Test
    @Transactional
    public void createTipoEvento() throws Exception {
        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().size();

        // Create the TipoEvento
        TipoEventoDTO tipoEventoDTO = tipoEventoMapper.toDto(tipoEvento);
        restTipoEventoMockMvc.perform(post("/api/tipo-eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoEventoDTO)))
            .andExpect(status().isCreated());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate + 1);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);

        // Validate the TipoEvento in Elasticsearch
        verify(mockTipoEventoSearchRepository, times(1)).save(testTipoEvento);
    }

    @Test
    @Transactional
    public void createTipoEventoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().size();

        // Create the TipoEvento with an existing ID
        tipoEvento.setId(1L);
        TipoEventoDTO tipoEventoDTO = tipoEventoMapper.toDto(tipoEvento);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTipoEventoMockMvc.perform(post("/api/tipo-eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoEventoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate);

        // Validate the TipoEvento in Elasticsearch
        verify(mockTipoEventoSearchRepository, times(0)).save(tipoEvento);
    }

    @Test
    @Transactional
    public void getAllTipoEventos() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        // Get all the tipoEventoList
        restTipoEventoMockMvc.perform(get("/api/tipo-eventos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoEvento.getId().intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }
    
    @Test
    @Transactional
    public void getTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        // Get the tipoEvento
        restTipoEventoMockMvc.perform(get("/api/tipo-eventos/{id}", tipoEvento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tipoEvento.getId().intValue()))
            .andExpect(jsonPath("$.descricao").value(DEFAULT_DESCRICAO.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTipoEvento() throws Exception {
        // Get the tipoEvento
        restTipoEventoMockMvc.perform(get("/api/tipo-eventos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();

        // Update the tipoEvento
        TipoEvento updatedTipoEvento = tipoEventoRepository.findById(tipoEvento.getId()).get();
        // Disconnect from session so that the updates on updatedTipoEvento are not directly saved in db
        em.detach(updatedTipoEvento);
        updatedTipoEvento
            .descricao(UPDATED_DESCRICAO);
        TipoEventoDTO tipoEventoDTO = tipoEventoMapper.toDto(updatedTipoEvento);

        restTipoEventoMockMvc.perform(put("/api/tipo-eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoEventoDTO)))
            .andExpect(status().isOk());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getDescricao()).isEqualTo(UPDATED_DESCRICAO);

        // Validate the TipoEvento in Elasticsearch
        verify(mockTipoEventoSearchRepository, times(1)).save(testTipoEvento);
    }

    @Test
    @Transactional
    public void updateNonExistingTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().size();

        // Create the TipoEvento
        TipoEventoDTO tipoEventoDTO = tipoEventoMapper.toDto(tipoEvento);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTipoEventoMockMvc.perform(put("/api/tipo-eventos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tipoEventoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);

        // Validate the TipoEvento in Elasticsearch
        verify(mockTipoEventoSearchRepository, times(0)).save(tipoEvento);
    }

    @Test
    @Transactional
    public void deleteTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);

        int databaseSizeBeforeDelete = tipoEventoRepository.findAll().size();

        // Get the tipoEvento
        restTipoEventoMockMvc.perform(delete("/api/tipo-eventos/{id}", tipoEvento.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the TipoEvento in Elasticsearch
        verify(mockTipoEventoSearchRepository, times(1)).deleteById(tipoEvento.getId());
    }

    @Test
    @Transactional
    public void searchTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.saveAndFlush(tipoEvento);
        when(mockTipoEventoSearchRepository.search(queryStringQuery("id:" + tipoEvento.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(tipoEvento), PageRequest.of(0, 1), 1));
        // Search the tipoEvento
        restTipoEventoMockMvc.perform(get("/api/_search/tipo-eventos?query=id:" + tipoEvento.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tipoEvento.getId().intValue())))
            .andExpect(jsonPath("$.[*].descricao").value(hasItem(DEFAULT_DESCRICAO.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoEvento.class);
        TipoEvento tipoEvento1 = new TipoEvento();
        tipoEvento1.setId(1L);
        TipoEvento tipoEvento2 = new TipoEvento();
        tipoEvento2.setId(tipoEvento1.getId());
        assertThat(tipoEvento1).isEqualTo(tipoEvento2);
        tipoEvento2.setId(2L);
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);
        tipoEvento1.setId(null);
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoEventoDTO.class);
        TipoEventoDTO tipoEventoDTO1 = new TipoEventoDTO();
        tipoEventoDTO1.setId(1L);
        TipoEventoDTO tipoEventoDTO2 = new TipoEventoDTO();
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
        tipoEventoDTO2.setId(tipoEventoDTO1.getId());
        assertThat(tipoEventoDTO1).isEqualTo(tipoEventoDTO2);
        tipoEventoDTO2.setId(2L);
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
        tipoEventoDTO1.setId(null);
        assertThat(tipoEventoDTO1).isNotEqualTo(tipoEventoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tipoEventoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tipoEventoMapper.fromId(null)).isNull();
    }
}
