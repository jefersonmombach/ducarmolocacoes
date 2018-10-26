package tech.jefersonms.ducarmolocacoes.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A TipoEvento.
 */
@Entity
@Table(name = "tipo_evento")
@Document(indexName = "tipoevento")
public class TipoEvento implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "descricao")
    private String descricao;

    @OneToMany(mappedBy = "tipoEvento")
    private Set<Locacao> locacoes = new HashSet<>();
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

    public TipoEvento descricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Locacao> getLocacoes() {
        return locacoes;
    }

    public TipoEvento locacoes(Set<Locacao> locacaos) {
        this.locacoes = locacaos;
        return this;
    }

    public TipoEvento addLocacoes(Locacao locacao) {
        this.locacoes.add(locacao);
        locacao.setTipoEvento(this);
        return this;
    }

    public TipoEvento removeLocacoes(Locacao locacao) {
        this.locacoes.remove(locacao);
        locacao.setTipoEvento(null);
        return this;
    }

    public void setLocacoes(Set<Locacao> locacaos) {
        this.locacoes = locacaos;
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
        TipoEvento tipoEvento = (TipoEvento) o;
        if (tipoEvento.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), tipoEvento.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TipoEvento{" +
            "id=" + getId() +
            ", descricao='" + getDescricao() + "'" +
            "}";
    }
}
