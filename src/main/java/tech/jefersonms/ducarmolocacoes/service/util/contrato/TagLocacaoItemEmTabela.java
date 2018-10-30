package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import org.springframework.util.CollectionUtils;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import tech.jefersonms.ducarmolocacoes.domain.Produto;

import java.util.ArrayList;
import java.util.List;

public class TagLocacaoItemEmTabela extends Tag {

	@Override
	protected String getFormattedValue(String tagName, Locacao locacao) {
		String retorno = "<table border=\"1\" cellpadding=\"1\" cellspacing=\"1\" style=\"height:20px; width:580px;border-collapse: collapse;\"><tbody>";
		if (!CollectionUtils.isEmpty(locacao.getProdutos())) {
			List<LocacaoProduto> itens = new ArrayList<>();
			itens.addAll(locacao.getProdutos());
			// adiciono 1 sempre que for impar para que a tabela sempre fique certa
			if (itens.size() % 2 != 0) {
				itens.add(new LocacaoProduto(new Produto(" ")));
			}
			for (int i = 0 ; i < itens.size() ; i++) {
				LocacaoProduto lp = itens.get(i);
				if (i % 2 == 0) {
					retorno += "<tr> <td><span style=\"font-size:11px\">"+lp.getProduto().getDescricao()+"</span></td>";
				} else {
					retorno += "<td><span style=\"font-size:11px\">"+lp.getProduto().getDescricao()+"</span></td> </tr>";
				}
			}
		}
		retorno += "</tbody> </table>";
		return retorno;
	}

}
