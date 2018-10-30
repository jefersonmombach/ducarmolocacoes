package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

import java.time.ZoneId;
import java.util.Date;

public class TagLocacaoDataEntregaPrevista extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getDataEntrPrev() != null) {
			return this.formatDate(Date.from(locacao.getDataEntrPrev().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else {
			return "";
		}
	}

}
