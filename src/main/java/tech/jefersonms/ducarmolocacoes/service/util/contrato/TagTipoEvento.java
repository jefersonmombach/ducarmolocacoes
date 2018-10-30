package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.domain.TipoEvento;

public class TagTipoEvento extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		TipoEvento te = locacao.getTipoEvento();
		if (te != null) {
			return locacao.getTipoEvento().getDescricao();
		} else {
			return "";
		}
	}

}
