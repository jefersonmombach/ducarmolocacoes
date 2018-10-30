package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import org.apache.commons.lang3.StringUtils;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagClienteCPF extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		String ret;
		if (locacao.getCliente() != null && locacao.getCliente().getCpf() != null) {
			ret = locacao.getCliente().getCpf();
		} else {
			ret = "";
		}
		return StringUtils.leftPad(ret, 20);
	}

}
