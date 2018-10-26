package tech.jefersonms.ducarmolocacoes.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the LocacaoProduto entity.
 */
public class LocacaoProdutoDTO implements Serializable {

    private Long id;

    private Integer quantidade;

    private BigDecimal valorUnitario;

    private BigDecimal valorTotal;

    private Long produtoId;

    private Long locacaoId;

    private Long clienteId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public Long getLocacaoId() {
        return locacaoId;
    }

    public void setLocacaoId(Long locacaoId) {
        this.locacaoId = locacaoId;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LocacaoProdutoDTO locacaoProdutoDTO = (LocacaoProdutoDTO) o;
        if (locacaoProdutoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), locacaoProdutoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "LocacaoProdutoDTO{" +
            "id=" + getId() +
            ", quantidade=" + getQuantidade() +
            ", valorUnitario=" + getValorUnitario() +
            ", valorTotal=" + getValorTotal() +
            ", produto=" + getProdutoId() +
            ", locacao=" + getLocacaoId() +
            ", cliente=" + getClienteId() +
            "}";
    }
}
