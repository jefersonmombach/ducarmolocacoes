package tech.jefersonms.ducarmolocacoes.service.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * A DTO for the Locacao entity.
 */
public class LocacaoDTO implements Serializable {

    private Long id;

    private BigDecimal valorTotal;

    private BigDecimal valorSinal;

    private BigDecimal valorAdiantado;

    private BigDecimal valorSaldo;

    private ZonedDateTime dataContratacao;

    private LocalDate dataEvento;

    private LocalDate dataDevPrev;

    private ZonedDateTime dataDevReal;

    private LocalDate dataEntrPrev;

    private ZonedDateTime dataEntrReal;

    private Integer situacao;

    private String htmlContrato;

    private Long clienteId;

    private Long tipoEventoId;

    private ClienteDTO cliente;

    private TipoEventoDTO tipoEvento;

    private List<LocacaoProdutoDTO> produtos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorSinal() {
        return valorSinal;
    }

    public void setValorSinal(BigDecimal valorSinal) {
        this.valorSinal = valorSinal;
    }

    public BigDecimal getValorAdiantado() {
        return valorAdiantado;
    }

    public void setValorAdiantado(BigDecimal valorAdiantado) {
        this.valorAdiantado = valorAdiantado;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public ZonedDateTime getDataContratacao() {
        return dataContratacao;
    }

    public void setDataContratacao(ZonedDateTime dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDate getDataDevPrev() {
        return dataDevPrev;
    }

    public void setDataDevPrev(LocalDate dataDevPrev) {
        this.dataDevPrev = dataDevPrev;
    }

    public ZonedDateTime getDataDevReal() {
        return dataDevReal;
    }

    public void setDataDevReal(ZonedDateTime dataDevReal) {
        this.dataDevReal = dataDevReal;
    }

    public LocalDate getDataEntrPrev() {
        return dataEntrPrev;
    }

    public void setDataEntrPrev(LocalDate dataEntrPrev) {
        this.dataEntrPrev = dataEntrPrev;
    }

    public ZonedDateTime getDataEntrReal() {
        return dataEntrReal;
    }

    public void setDataEntrReal(ZonedDateTime dataEntrReal) {
        this.dataEntrReal = dataEntrReal;
    }

    public Integer getSituacao() {
        return situacao;
    }

    public void setSituacao(Integer situacao) {
        this.situacao = situacao;
    }

    public String getHtmlContrato() {
        return htmlContrato;
    }

    public void setHtmlContrato(String htmlContrato) {
        this.htmlContrato = htmlContrato;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getTipoEventoId() {
        return tipoEventoId;
    }

    public void setTipoEventoId(Long tipoEventoId) {
        this.tipoEventoId = tipoEventoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocacaoDTO locacaoDTO = (LocacaoDTO) o;
        if (locacaoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locacaoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LocacaoDTO{" +
            "id=" + getId() +
            ", valorTotal=" + getValorTotal() +
            ", valorSinal=" + getValorSinal() +
            ", valorAdiantado=" + getValorAdiantado() +
            ", valorSaldo=" + getValorSaldo() +
            ", dataContratacao='" + getDataContratacao() + "'" +
            ", dataEvento='" + getDataEvento() + "'" +
            ", dataDevPrev='" + getDataDevPrev() + "'" +
            ", dataDevReal='" + getDataDevReal() + "'" +
            ", dataEntrPrev='" + getDataEntrPrev() + "'" +
            ", dataEntrReal='" + getDataEntrReal() + "'" +
            ", situacao=" + getSituacao() +
            ", htmlContrato='" + getHtmlContrato() + "'" +
            ", cliente=" + getClienteId() +
            ", tipoEvento=" + getTipoEventoId() +
            ", produtos=" + (getProdutos() != null ? " " + getProdutos().size() : "null") +
            "}";
    }

    public ClienteDTO getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDTO cliente) {
        this.cliente = cliente;
    }

    public TipoEventoDTO getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEventoDTO tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public List<LocacaoProdutoDTO> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<LocacaoProdutoDTO> produtos) {
        this.produtos = produtos;
    }
}
