package tech.jefersonms.ducarmolocacoes.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import tech.jefersonms.ducarmolocacoes.domain.EnumSituacaoLocacao;
import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import tech.jefersonms.ducarmolocacoes.repository.ClienteRepository;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoProdutoRepository;
import tech.jefersonms.ducarmolocacoes.repository.TipoEventoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoProdutoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.LocacaoService;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.repository.LocacaoRepository;
import tech.jefersonms.ducarmolocacoes.repository.search.LocacaoSearchRepository;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoDTO;
import tech.jefersonms.ducarmolocacoes.service.dto.LocacaoProdutoDTO;
import tech.jefersonms.ducarmolocacoes.service.dto.ProdutoDTO;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jefersonms.ducarmolocacoes.service.mapper.LocacaoProdutoMapper;
import tech.jefersonms.ducarmolocacoes.service.util.contrato.TagProcessor;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private ClienteRepository clienteRepository;
    private TipoEventoRepository tipoEventoRepository;
    private LocacaoProdutoRepository locacaoProdutoRepository;
    private LocacaoProdutoMapper locacaoProdutoMapper;
    private LocacaoProdutoSearchRepository locacaoProdutoSearchRepository;

    private static final String HTML_CONTRATO = "<p style=\"text-align:center\"><span style=\"font-size:16px\"><strong>CONTRATO DE LOCA&Ccedil;&Atilde;O</strong></span></p> <p style=\"text-align:center\">&nbsp;</p> <p style=\"text-align:center\"><span style=\"font-size:16px\"><strong><span style=\"color:#0000cd\">Du Carmo Loca&ccedil;&otilde;es</span></strong></span></p> <p style=\"text-align:center\">&nbsp;</p> <p style=\"text-align:center\"><span style=\"font-size:14px\"><strong><em>Locadora: Maria do Carmo Poli, brasileira, casada, do com&eacute;rcio, com endere&ccedil;o &agrave;</em></strong></span></p> <p style=\"text-align:center\"><span style=\"font-size:14px\"><strong><em>Rua Benjamin Constant Teixeira, 929 - Centro - Bocai&uacute;va do Sul - PR</em></strong></span></p> <p style=\"text-align:center\"><span style=\"font-size:14px\"><strong><em>Fone: 8415-4842 - 99933-2404</em></strong></span></p> <p style=\"text-align:center\">&nbsp;</p> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>Nome:</strong> TAG_CLIENTE_NOME</span></td> <td style=\"text-align:right\"><span style=\"font-size:14px\"><strong>Tipo de Evento:</strong> TAG_TIPO_EVENTO</span></td> </tr> </tbody> </table> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>CPF: </strong>TAG_CLIENTE_CPF</span></td> <td style=\"text-align:center\"><span style=\"font-size:14px\"><strong>RG: </strong>TAG_CLIENTE_RG</span></td> <td style=\"text-align:right\"><span style=\"font-size:14px\"><strong>Nascimento:</strong> TAG_CLIENTE_NASCIMENTO</span></td> </tr> </tbody> </table> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>Endere&ccedil;o:</strong> TAG_CLIENTE_ENDERECO</span></td> </tr> </tbody> </table> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>Tel.Res.: </strong>TAG_CLIENTE_FONE_RESIDENCIAL</span></td> <td style=\"text-align:center\"><span style=\"font-size:14px\"><strong>Tel.Com.:</strong> TAG_CLIENTE_FONE_COMERCIAL</span></td> <td style=\"text-align:right\"><span style=\"font-size:14px\"><strong>Tel.Rec.: </strong>TAG_CLIENTE_FONE_RECADO</span></td> </tr> </tbody> </table> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>TOTAL:</strong> R$ TAG_LOCACAO_VALOR_TOTAL</span></td> <td style=\"text-align:center\"><span style=\"font-size:14px\"><strong>SINAL:</strong> R$ TAG_LOCACAO_VALOR_SINAL</span></td> <td style=\"text-align:center\"><span style=\"font-size:14px\"><strong>ADIANTADO:</strong> R$ TAG_LOCACAO_VALOR_ADIANTADO</span></td> <td style=\"text-align:right\"><span style=\"font-size:14px\"><strong>SALDO</strong> R$: TAG_LOCACAO_VALOR_SALDO</span></td> </tr> </tbody> </table> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td><span style=\"font-size:14px\"><strong>DATA DA RETIRADA: TAG_LOCACAO_DATA_ENTREGA_PREVISTA (AP&Oacute;S 16HS)</strong></span></td> <td style=\"text-align:right\"><span style=\"font-size:14px\"><strong>DATA DEVOLU&Ccedil;&Atilde;O: TAG_LOCACAO_DATA_DEVOLUCAO_PREVISTA</strong></span></td> </tr> </tbody> </table> <p style=\"text-align:center\"><span style=\"font-size:14px\"><strong>* O SALDO DEVEDOR DEVER&Aacute; SER PAGO <u>NA RETIRADA DO TRAJE</u> </strong></span></p> <p style=\"text-align:center\">&nbsp;</p> <p style=\"text-align:center\"><span style=\"font-size:14px\"><strong>Locadora e Locat&aacute;rio firmam este contrato e se responsabilizam a cumprir mutuamente as cl&aacute;usulas abaixo descritas:</strong></span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">1. O Locat&aacute;rio utilizar&aacute; os seguintes trajes:</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">TAG_LOCACAO_ITEM_EM_TABELA</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">2. O Valor total da loca&ccedil;&atilde;o &eacute; de que dever&aacute; ser pago da seguinte forma:</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">a) O Locat&aacute;rio dever&aacute; <strong><span style=\"background-color:#ffff66\">dar um sinal de 20% do valor do traje para que a reserva seja efetivada</span></strong>, que ser&aacute; descontado na data do pagamento total da loca&ccedil;&atilde;o no dia da retirada.</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\"><span style=\"background-color:#ffff66\">Caso houver arrependimento do neg&oacute;cio, perder&aacute; o valor do sinal em proveito do outro e dever&aacute; ser comunicado imediatamente a locadora, caso n&atilde;o seja comunicado a desist&ecirc;ncia no m&aacute;ximo 10 dias antecedentes do evento, ser&aacute; cobrado normalmente o valor da loca&ccedil;&atilde;o. Se o arrependimento foi do quem recebeu restitu&iacute;-las em dobro do valor do sinal do traje em quest&atilde;o.</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">b) <strong><span style=\"background-color:#ffff66\">Em caso de troca por traje </span><u><span style=\"background-color:#ffff66\">para outra pessoa</span></u><span style=\"background-color:#ffff66\">, o valor do sinal pago neste traje n&atilde;o ser&aacute; descontado no valor outro traje, considera-se arrependimento do neg&oacute;cio, ou seja, perde o sinal</span></strong>. Somente o valor adiantado (se houver) ser&aacute; descontado no novo para outra pessoa traje. </span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">c) <strong><u>O Saldo devedor dever&aacute; ser quitado at&eacute; a data da retirada do traje.</u></strong></span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">3. Os trajes e/ou acess&oacute;rios ser&atilde;o entregues em perfeitas condi&ccedil;&otilde;es de uso, <strong><span style=\"background-color:#ffff66\">comprometendo-se o locat&aacute;rio a devolve-los sem danos</span>, responsabilizando-se por quaisquer eventualidades que possam ocorrer, tais como: rasgar, queimar, manchar, furar, entre outros, se houver danos o valor do traje dever&aacute; ser restitu&iacute;do calculando-se quatro vezes o valor do aluguel, sendo que o valor pago na loca&ccedil;&atilde;o n&atilde;o entra no c&aacute;lculo para pagamento do dano causado.</strong> </span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">4. Fica proibido qualquer tipo de manuten&ccedil;&atilde;o nos trajes como: <strong>lavar, passar, bordar, ajustar, costurar, <u>se necess&aacute;rio dever&aacute; ser comunicado &agrave; locadora.</u> </strong> </span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">5. <strong>Os trajes dever&atilde;o ser entregues na data combinda com a locadora, do contr&aacute;rio o locat&aacute;rio dever&aacute; pagar multa de 20% do valor da loca&ccedil;&atilde;o por dia de atraso</strong>, salvo combinado previamente com a locadora justificando o atraso. </span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">6. A locadora n&atilde;o se responsabiliza por trajes n&atilde;o provados ou trajes locados a terceiros.</span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">7. <strong>A locadora fica autorizada a n&atilde;o <u>entregar</u> os trajes caso n&atilde;o seja efetuado o pagamento do saldo devedor at&eacute; a data da retirada, ocorrendo o cancelamento autom&aacute;tico do contrato, e o locat&aacute;rio perde o sinal pago. </strong></span></p> <p style=\"text-align:justify\"><span style=\"font-size:12px\">E por estarem assim juntos e contratados de pleno acordo com todas as cl&aacute;usulas e condi&ccedil;&otilde;es acima estipuladas, assina este instrumento particular que foi lido, analisado e assinado pelas partes, para que produza seus efeitos jur&iacute;dicos legais.</span></p> <p style=\"text-align:justify\">&nbsp;</p> <table border=\"0\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:29px; width:580px\"> <tbody> <tr> <td style=\"text-align:left\"><span style=\"font-size:12px\"><strong>Locador</strong></span></td> <td style=\"text-align:center\"><span style=\"font-size:12px\"><strong>Bocai&uacute;va do Sul, TAG_LOCACAO_DATA_CONTRATACAO</strong></span></td> <td style=\"text-align:right\"><span style=\"font-size:12px\"><strong>Locat&aacute;rio</strong></span></td> </tr> </tbody> </table> <p style=\"text-align:center\"><span style=\"font-size:12px\"><strong>Hor&aacute;rio de Atendimento</strong></span></p> <p style=\"text-align:center\"><span style=\"font-size:12px\">Segunda &agrave; Sexta das 10:00 &agrave;s 18:00 (com intervalor para almo&ccedil;o)</span></p> <p style=\"text-align:center\"><span style=\"font-size:12px\"><strong>Atendo ap&oacute;s o expediente com hor&aacute;rio agendado.</strong></span></p>";

    @Autowired
    private ObjectMapper objectMapper;

    public LocacaoServiceImpl(LocacaoRepository locacaoRepository,
                              LocacaoMapper locacaoMapper,
                              LocacaoSearchRepository locacaoSearchRepository,
                              ClienteRepository clienteRepository,
                              TipoEventoRepository tipoEventoRepository,
                              LocacaoProdutoRepository locacaoProdutoRepository,
                              LocacaoProdutoMapper locacaoProdutoMapper,
                              LocacaoProdutoSearchRepository locacaoProdutoSearchRepository) {
        this.locacaoRepository = locacaoRepository;
        this.locacaoMapper = locacaoMapper;
        this.locacaoSearchRepository = locacaoSearchRepository;
        this.clienteRepository = clienteRepository;
        this.tipoEventoRepository = tipoEventoRepository;
        this.locacaoProdutoRepository = locacaoProdutoRepository;
        this.locacaoProdutoMapper = locacaoProdutoMapper;
        this.locacaoProdutoSearchRepository = locacaoProdutoSearchRepository;
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

        locacao.setCliente(clienteRepository.findById(locacao.getCliente().getId()).get());
        locacao.setTipoEvento(tipoEventoRepository.findById(locacao.getTipoEvento().getId()).get());

        if (locacao.getSituacao().compareTo(EnumSituacaoLocacao.CONTRATO_EMITIDO.getId()) == 0) {
            locacao.setHtmlContrato(gerarHTMLContrato(locacao));
        } else if (locacao.getSituacao().compareTo(EnumSituacaoLocacao.TRAJES_ENTREGUES.getId()) == 0) {
            locacao.setDataEntrReal(ZonedDateTime.now());
        } else if (locacao.getSituacao().compareTo(EnumSituacaoLocacao.TRAJES_DEVOLVIDOS.getId()) == 0) {
            locacao.setDataDevReal(ZonedDateTime.now());
        }

        locacao = locacaoRepository.save(locacao);
        LocacaoDTO result = locacaoMapper.toDto(locacao);
        locacaoSearchRepository.save(locacao);

        this.saveProdutos(result, locacaoDTO.getProdutos());

        return result;
    }

    private List<LocacaoProdutoDTO> saveProdutos(LocacaoDTO locacaoDTO, List<LocacaoProdutoDTO> locacaoProdutoDTO) {
        List<LocacaoProdutoDTO> result = new ArrayList<>();

        for (LocacaoProdutoDTO lpDTO : locacaoProdutoDTO) {
            lpDTO.setLocacao(locacaoDTO);
            lpDTO.setLocacaoId(locacaoDTO.getId());
            result.add(this.saveProduto(lpDTO));
        }

        return result;
    }

    private LocacaoProdutoDTO saveProduto(LocacaoProdutoDTO locacaoProdutoDTO) {
        log.debug("Request to save LocacaoProduto : {}", locacaoProdutoDTO);

        LocacaoProduto locacaoProduto = locacaoProdutoMapper.toEntity(locacaoProdutoDTO);
        locacaoProduto.setCliente(clienteRepository.findById(locacaoProdutoDTO.getClienteId()).get());
        locacaoProduto = locacaoProdutoRepository.save(locacaoProduto);
        LocacaoProdutoDTO result = locacaoProdutoMapper.toDto(locacaoProduto);
        locacaoProdutoSearchRepository.save(locacaoProduto);

        return result;
    }

    private String gerarHTMLContrato(Locacao locacao) {
        String html = TagProcessor.getInstance().processarTags(this.HTML_CONTRATO, locacao);
        return html;
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
            .map(locacaoMapper::toDto)
            .map(this::getProdutos);
    }

    private <U extends LocacaoDTO> U getProdutos(LocacaoDTO locacaoDTO) {
        locacaoDTO.setProdutos(locacaoProdutoRepository.findByLocacao(locacaoMapper.toEntity(locacaoDTO)).stream()
            .map(locacaoProdutoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new)));
        return (U) locacaoDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LocacaoDTO> listLocacoesProdutoByDataEvento(ProdutoDTO produto, LocalDate dataEvento) {
        return this.locacaoProdutoRepository.listAllLocacoes(dataEvento.minusDays(3), dataEvento.plusDays(4))
            .stream()
            .filter(p -> p.getProduto().getId() == produto.getId())
            .map(p -> p.getLocacao())
            .distinct()
            .map(l -> locacaoMapper.toDto(l))
            .collect(Collectors.toList());
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
     * Patch the locacao by id.
     *
     * @param id the id of the entity
     */
    @Override
    public LocacaoDTO patch(Long id, Map<String, String> fields) {
        log.debug("Request to patch Locacao : {}", id);
        Optional<Locacao> opt = locacaoRepository.findById(id);

        if (!opt.isPresent()) {
            return new LocacaoDTO();
        }

        LocacaoDTO locacaoDTO = locacaoMapper.toDto(opt.get());

        fields.forEach((k, v) -> {
            // use reflection to get field k on manager and set it to value k
            Field field = ReflectionUtils.findField(LocacaoDTO.class, k);
            ReflectionUtils.setField(field, locacaoDTO, v);
        });

        return this.save(locacaoDTO);
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
