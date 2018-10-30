package tech.jefersonms.ducarmolocacoes.domain;

import java.util.Arrays;

public enum EnumSituacaoLocacao {

    NOVO(0, "Novo"),
    CONTRATO_EMITIDO(1, "Contrato Emitido"),
    TRAJES_ENTREGUES(2, "Trajes Entreges"),
    TRAJES_DEVOLVIDOS(3, "Trajes Devolvidos"),
    CANCELADO(9, "Cancelado"),
    NAO_IDENTIFICADO(-99999, null);

    private Integer id;
    private String descricao;

    EnumSituacaoLocacao(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public static EnumSituacaoLocacao findById(Integer id) {
        return Arrays.asList(EnumSituacaoLocacao.values()).stream().filter(p -> p.id == id).findFirst().orElse(null);
    }

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

}
