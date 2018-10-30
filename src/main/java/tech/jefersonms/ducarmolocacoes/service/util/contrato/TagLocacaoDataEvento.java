package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.service.util.DateUtil;

import java.time.ZoneId;
import java.util.Date;

public class TagLocacaoDataEvento extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		if (locacao.getDataEvento() != null) {
            Date data = Date.from(locacao.getDataEvento().atStartOfDay(ZoneId.systemDefault()).toInstant());

			return DateUtil.formatDateDDMMYYYY(data);
		} else {
			return "";
		}
	}

}
