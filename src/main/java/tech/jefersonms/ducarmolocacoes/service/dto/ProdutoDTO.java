package tech.jefersonms.ducarmolocacoes.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the Produto entity.
 */
public class ProdutoDTO implements Serializable {

    private Long id;

    private String descricao;

    private BigDecimal precoVenda;

    private BigDecimal precoCompra;

    private Boolean ativo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPrecoVenda() {
        return precoVenda;
    }

    public void setPrecoVenda(BigDecimal precoVenda) {
        this.precoVenda = precoVenda;
    }

    public BigDecimal getPrecoCompra() {
        return precoCompra;
    }

    public void setPrecoCompra(BigDecimal precoCompra) {
        this.precoCompra = precoCompra;
    }

    public Boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ProdutoDTO produtoDTO = (ProdutoDTO) o;
        if (produtoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), produtoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ProdutoDTO{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            ", precoVenda=" + getPrecoVenda() +
            ", precoCompra=" + getPrecoCompra() +
            ", ativo='" + isAtivo() + "'" +
            "}";
    }
}
