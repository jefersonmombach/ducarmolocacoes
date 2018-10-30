package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoValorSaldo extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getValorSaldo() != null) {
			return this.formatNumber2c(locacao.getValorSaldo());
		} else {
			return "";
		}
	}

}
