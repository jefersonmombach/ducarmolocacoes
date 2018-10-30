package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteFoneResidencial extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getCliente() != null && locacao.getCliente().getTelefoneResidencial() != null) {
			return locacao.getCliente().getTelefoneResidencial();
		} else {
			return "";
		}
	}

}
