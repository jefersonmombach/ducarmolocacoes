package tech.jefersonms.ducarmolocacoes.service.dto;

import java.time.LocalDate;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Cliente entity.
 */
public class ClienteDTO implements Serializable {

    private Long id;

    private String nome;

    private LocalDate nascimento;

    private String cpf;

    private String rg;

    private String telefoneResidencial;

    private String telefoneCelular;

    private String telefoneRecado;

    private String endereco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getNascimento() {
        return nascimento;
    }

    public void setNascimento(LocalDate nascimento) {
        this.nascimento = nascimento;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getTelefoneResidencial() {
        return telefoneResidencial;
    }

    public void setTelefoneResidencial(String telefoneResidencial) {
        this.telefoneResidencial = telefoneResidencial;
    }

    public String getTelefoneCelular() {
        return telefoneCelular;
    }

    public void setTelefoneCelular(String telefoneCelular) {
        this.telefoneCelular = telefoneCelular;
    }

    public String getTelefoneRecado() {
        return telefoneRecado;
    }

    public void setTelefoneRecado(String telefoneRecado) {
        this.telefoneRecado = telefoneRecado;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClienteDTO clienteDTO = (ClienteDTO) o;
        if (clienteDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), clienteDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClienteDTO{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", nascimento='" + getNascimento() + "'" +
            ", cpf='" + getCpf() + "'" +
            ", rg='" + getRg() + "'" +
            ", telefoneResidencial='" + getTelefoneResidencial() + "'" +
            ", telefoneCelular='" + getTelefoneCelular() + "'" +
            ", telefoneRecado='" + getTelefoneRecado() + "'" +
            ", endereco='" + getEndereco() + "'" +
            "}";
    }
}
