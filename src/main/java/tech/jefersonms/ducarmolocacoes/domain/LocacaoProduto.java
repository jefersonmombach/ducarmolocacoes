package tech.jefersonms.ducarmolocacoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A LocacaoProduto.
 */
@Entity
@Table(name = "locacao_produto")
@Document(indexName = "locacaoproduto")
public class LocacaoProduto implements Serializable {

    private static final long serialVersionUID = 1L;

    public LocacaoProduto() {

    }

    public LocacaoProduto(Produto produto) {
        this.produto = produto;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quantidade")
    private Integer quantidade;

    @Column(name = "valor_unitario", precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @ManyToOne
    @JsonIgnoreProperties("locacoes")
    private Produto produto;

    @ManyToOne
    @JsonIgnoreProperties("produtos")
    private Locacao locacao;

    @ManyToOne
    @JsonIgnoreProperties("produtos")
    private Cliente cliente;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public LocacaoProduto quantidade(Integer quantidade) {
        this.quantidade = quantidade;
        return this;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public LocacaoProduto valorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
        return this;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocacaoProduto valorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
        return this;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Produto getProduto() {
        return produto;
    }

    public LocacaoProduto produto(Produto produto) {
        this.produto = produto;
        return this;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Locacao getLocacao() {
        return locacao;
    }

    public LocacaoProduto locacao(Locacao locacao) {
        this.locacao = locacao;
        return this;
    }

    public void setLocacao(Locacao locacao) {
        this.locacao = locacao;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocacaoProduto cliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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
        LocacaoProduto locacaoProduto = (LocacaoProduto) o;
        if (locacaoProduto.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locacaoProduto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LocacaoProduto{" +
            "id=" + getId() +
            ", quantidade=" + getQuantidade() +
            ", valorUnitario=" + getValorUnitario() +
            ", valorTotal=" + getValorTotal() +
            "}";
    }
}
