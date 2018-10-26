package tech.jefersonms.ducarmolocacoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Produto.
 */
@Entity
@Table(name = "produto")
@Document(indexName = "produto")
public class Produto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "preco_venda", precision = 10, scale = 2)
    private BigDecimal precoVenda;

    @Column(name = "preco_compra", precision = 10, scale = 2)
    private BigDecimal precoCompra;

    @Column(name = "ativo")
    private Boolean ativo;

    @OneToMany(mappedBy = "produto")
    private Set<LocacaoProduto> locacoes = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public Produto descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public Produto precoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
        return this;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public Produto precoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
        return this;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public Boolean isAtivo() {
        return ativo;
    }

    public Produto ativo(Boolean ativo) {
        this.ativo = ativo;
        return this;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Set<LocacaoProduto> getLocacoes() {
        return locacoes;
    }

    public Produto locacoes(Set<LocacaoProduto> locacaoProdutos) {
        this.locacoes = locacaoProdutos;
        return this;
    }

    public Produto addLocacoes(LocacaoProduto locacaoProduto) {
        this.locacoes.add(locacaoProduto);
        locacaoProduto.setProduto(this);
        return this;
    }

    public Produto removeLocacoes(LocacaoProduto locacaoProduto) {
        this.locacoes.remove(locacaoProduto);
        locacaoProduto.setProduto(null);
        return this;
    }

    public void setLocacoes(Set<LocacaoProduto> locacaoProdutos) {
        this.locacoes = locacaoProdutos;
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
        Produto produto = (Produto) o;
        if (produto.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), produto.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Produto{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", precoVenda=" + getPrecoVenda() +
            ", precoCompra=" + getPrecoCompra() +
            ", ativo='" + isAtivo() + "'" +
            "}";
    }
}
