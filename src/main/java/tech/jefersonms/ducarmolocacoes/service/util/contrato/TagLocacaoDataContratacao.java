package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

import java.time.ZoneId;
import java.util.Date;

public class TagLocacaoDataContratacao extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getDataContratacao() != null) {
			return this.formatDateSemiExtended(Date.from(locacao.getDataContratacao().toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else {
			return "";
		}
	}

}
