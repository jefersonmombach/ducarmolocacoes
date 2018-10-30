package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoValorTotal extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getValorTotal() != null) {
			return this.formatNumber2c(locacao.getValorTotal());
		} else {
			return "null";
		}
	}

}
