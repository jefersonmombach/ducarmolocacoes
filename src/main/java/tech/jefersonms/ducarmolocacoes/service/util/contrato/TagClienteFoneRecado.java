package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteFoneRecado extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getCliente() != null && locacao.getCliente().getTelefoneRecado() != null) {
			return locacao.getCliente().getTelefoneRecado();
		} else {
			return "";
		}
	}

}
