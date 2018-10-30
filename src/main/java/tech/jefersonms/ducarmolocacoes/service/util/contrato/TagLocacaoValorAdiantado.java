package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoValorAdiantado extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getValorAdiantado() != null) {
			return this.formatNumber2c(locacao.getValorAdiantado());
		} else {
			return "";
		}
	}

}
