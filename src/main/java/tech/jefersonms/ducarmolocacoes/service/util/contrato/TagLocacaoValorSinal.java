package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagLocacaoValorSinal extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getValorSinal() != null) {
			return this.formatNumber2c(locacao.getValorSinal());
		} else {
			return "";
		}
	}

}
