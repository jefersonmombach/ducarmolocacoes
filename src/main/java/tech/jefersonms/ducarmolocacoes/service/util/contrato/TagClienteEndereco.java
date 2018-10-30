package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteEndereco extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getCliente() != null && locacao.getCliente().getEndereco() != null) {
			return locacao.getCliente().getEndereco();
		} else {
			return "";
		}
	}

}
