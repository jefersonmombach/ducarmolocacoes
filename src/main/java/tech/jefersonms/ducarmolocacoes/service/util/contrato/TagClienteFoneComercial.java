package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteFoneComercial extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getCliente() != null && locacao.getCliente().getTelefoneCelular() != null) {
			return locacao.getCliente().getTelefoneCelular();
		} else {
			return "";
		}
	}

}
