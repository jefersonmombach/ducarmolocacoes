package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class TagClienteNascimento extends Tag {
	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		LocalDate n = locacao.getCliente().getNascimento();
		if (n != null) {
			return this.formatDate(Date.from(n.atStartOfDay(ZoneId.systemDefault()).toInstant()));
		} else {
			return "";
		}
	}
}
