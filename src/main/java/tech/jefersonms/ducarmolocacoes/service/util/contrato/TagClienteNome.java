package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;

import java.util.HashSet;
import java.util.Set;

public class TagClienteNome extends Tag {

    private final Logger log = LoggerFactory.getLogger(TagClienteNome.class);

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		StringBuilder buf = new StringBuilder();
		Set<String> nomes = new HashSet<>();

        if (locacao.getCliente() != null && locacao.getCliente().getNome() != null) {
			nomes.add(locacao.getCliente().getNome());
			
			if (locacao.getProdutos() != null) {
				locacao.getProdutos().stream().forEach(lp -> nomes.add(lp.getCliente().getNome()));
			}
			
			nomes.stream().forEach(nome -> buf.append(StringUtils.trim(nome)+"/"));
		}
		
		String ret = buf.toString();
		
		return StringUtils.rightPad(ret.substring(0, ret.length()-1), 100);
	}

}
