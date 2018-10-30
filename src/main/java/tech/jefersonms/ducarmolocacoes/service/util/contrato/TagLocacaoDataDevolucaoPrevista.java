package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

import java.time.ZoneId;
import java.util.Date;

public class TagLocacaoDataDevolucaoPrevista extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getDataDevPrev() != null) {
			return this.formatDate(Date.from(locacao.getDataDevPrev().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else {
			return "";
		}
	}

}
