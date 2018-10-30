package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import org.apache.commons.lang3.StringUtils;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteRG extends Tag {
	
	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		String ret;
		if (locacao.getCliente() != null && locacao.getCliente().getRg() != null) {
			ret = locacao.getCliente().getRg();
		} else {
			ret = "";
		}
		return StringUtils.leftPad(ret, 20);
	}
	
}
