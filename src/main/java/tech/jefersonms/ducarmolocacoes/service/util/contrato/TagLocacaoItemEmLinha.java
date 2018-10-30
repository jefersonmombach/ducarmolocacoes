package tech.jefersonms.ducarmolocacoes.service.util.contrato;


import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoItemEmLinha extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		return "Lista de Itens em Linha";
	}

}
