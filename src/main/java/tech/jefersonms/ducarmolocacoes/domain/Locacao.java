package tech.jefersonms.ducarmolocacoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Locacao.
 */
@Entity
@Table(name = "locacao")
@Document(indexName = "locacao")
public class Locacao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "valor_sinal", precision = 10, scale = 2)
    private BigDecimal valorSinal;

    @Column(name = "valor_adiantado", precision = 10, scale = 2)
    private BigDecimal valorAdiantado;

    @Column(name = "valor_saldo", precision = 10, scale = 2)
    private BigDecimal valorSaldo;

    @Column(name = "data_contratacao")
    private ZonedDateTime dataContratacao;

    @Column(name = "data_evento")
    private LocalDate dataEvento;

    @Column(name = "data_dev_prev")
    private LocalDate dataDevPrev;

    @Column(name = "data_dev_real")
    private ZonedDateTime dataDevReal;

    @Column(name = "data_entr_prev")
    private LocalDate dataEntrPrev;

    @Column(name = "data_entr_real")
    private ZonedDateTime dataEntrReal;

    @Column(name = "situacao")
    private Integer situacao;

    @Column(name = "html_contrato")
    private String htmlContrato;

    @OneToMany(mappedBy = "locacao")
    private Set<LocacaoProduto> produtos = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("locacoes")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnoreProperties("locacoes")
    private TipoEvento tipoEvento;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public Locacao valorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
        return this;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public BigDecimal getValorSinal() {
        return valorSinal;
    }

    public Locacao valorSinal(BigDecimal valorSinal) {
        this.valorSinal = valorSinal;
        return this;
    }

    public void setValorSinal(BigDecimal valorSinal) {
        this.valorSinal = valorSinal;
    }

    public BigDecimal getValorAdiantado() {
        return valorAdiantado;
    }

    public Locacao valorAdiantado(BigDecimal valorAdiantado) {
        this.valorAdiantado = valorAdiantado;
        return this;
    }

    public void setValorAdiantado(BigDecimal valorAdiantado) {
        this.valorAdiantado = valorAdiantado;
    }

    public BigDecimal getValorSaldo() {
        return valorSaldo;
    }

    public Locacao valorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
        return this;
    }

    public void setValorSaldo(BigDecimal valorSaldo) {
        this.valorSaldo = valorSaldo;
    }

    public ZonedDateTime getDataContratacao() {
        return dataContratacao;
    }

    public Locacao dataContratacao(ZonedDateTime dataContratacao) {
        this.dataContratacao = dataContratacao;
        return this;
    }

    public void setDataContratacao(ZonedDateTime dataContratacao) {
        this.dataContratacao = dataContratacao;
    }

    public LocalDate getDataEvento() {
        return dataEvento;
    }

    public Locacao dataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
        return this;
    }

    public void setDataEvento(LocalDate dataEvento) {
        this.dataEvento = dataEvento;
    }

    public LocalDate getDataDevPrev() {
        return dataDevPrev;
    }

    public Locacao dataDevPrev(LocalDate dataDevPrev) {
        this.dataDevPrev = dataDevPrev;
        return this;
    }

    public void setDataDevPrev(LocalDate dataDevPrev) {
        this.dataDevPrev = dataDevPrev;
    }

    public ZonedDateTime getDataDevReal() {
        return dataDevReal;
    }

    public Locacao dataDevReal(ZonedDateTime dataDevReal) {
        this.dataDevReal = dataDevReal;
        return this;
    }

    public void setDataDevReal(ZonedDateTime dataDevReal) {
        this.dataDevReal = dataDevReal;
    }

    public LocalDate getDataEntrPrev() {
        return dataEntrPrev;
    }

    public Locacao dataEntrPrev(LocalDate dataEntrPrev) {
        this.dataEntrPrev = dataEntrPrev;
        return this;
    }

    public void setDataEntrPrev(LocalDate dataEntrPrev) {
        this.dataEntrPrev = dataEntrPrev;
    }

    public ZonedDateTime getDataEntrReal() {
        return dataEntrReal;
    }

    public Locacao dataEntrReal(ZonedDateTime dataEntrReal) {
        this.dataEntrReal = dataEntrReal;
        return this;
    }

    public void setDataEntrReal(ZonedDateTime dataEntrReal) {
        this.dataEntrReal = dataEntrReal;
    }

    public Integer getSituacao() {
        return situacao;
    }

    public Locacao situacao(Integer situacao) {
        this.situacao = situacao;
        return this;
    }

    public void setSituacao(Integer situacao) {
        this.situacao = situacao;
    }

    public String getHtmlContrato() {
        return htmlContrato;
    }

    public Locacao htmlContrato(String htmlContrato) {
        this.htmlContrato = htmlContrato;
        return this;
    }

    public void setHtmlContrato(String htmlContrato) {
        this.htmlContrato = htmlContrato;
    }

    public Set<LocacaoProduto> getProdutos() {
        return produtos;
    }

    public Locacao produtos(Set<LocacaoProduto> locacaoProdutos) {
        this.produtos = locacaoProdutos;
        return this;
    }

    public Locacao addProdutos(LocacaoProduto locacaoProduto) {
        this.produtos.add(locacaoProduto);
        locacaoProduto.setLocacao(this);
        return this;
    }

    public Locacao removeProdutos(LocacaoProduto locacaoProduto) {
        this.produtos.remove(locacaoProduto);
        locacaoProduto.setLocacao(null);
        return this;
    }

    public void setProdutos(Set<LocacaoProduto> locacaoProdutos) {
        this.produtos = locacaoProdutos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Locacao cliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public Locacao tipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
        return this;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Locacao locacao = (Locacao) o;
        if (locacao.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locacao.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Locacao{" +
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
            "}";
    }
}
