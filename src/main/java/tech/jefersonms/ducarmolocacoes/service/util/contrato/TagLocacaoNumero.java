package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoNumero extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		return locacao.getId().toString();
	}

}
