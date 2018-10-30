package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum EnumModeloContratoTag {

	TAG_LOCACAO_NUMERO(TagLocacaoNumero.class),
	TAG_LOCACAO_VALOR_ADIANTADO(TagLocacaoValorAdiantado.class),
	TAG_LOCACAO_VALOR_TOTAL(TagLocacaoValorTotal.class),
	TAG_LOCACAO_VALOR_SINAL(TagLocacaoValorSinal.class),
	TAG_LOCACAO_VALOR_SALDO(TagLocacaoValorSaldo.class),
	TAG_LOCACAO_DATA_CONTRATACAO(TagLocacaoDataContratacao.class),
	TAG_LOCACAO_DATA_EVENTO(TagLocacaoDataEvento.class),
	TAG_LOCACAO_DATA_DEVOLUCAO_PREVISTA(TagLocacaoDataDevolucaoPrevista.class),
	TAG_LOCACAO_DATA_ENTREGA_PREVISTA(TagLocacaoDataEntregaPrevista.class),
	
	TAG_CLIENTE_NOME(TagClienteNome.class),
	TAG_CLIENTE_FONE_RESIDENCIAL(TagClienteFoneResidencial.class),
	TAG_CLIENTE_FONE_COMERCIAL(TagClienteFoneComercial.class),
	TAG_CLIENTE_FONE_RECADO(TagClienteFoneRecado.class),
	TAG_CLIENTE_ENDERECO(TagClienteEndereco.class),
	TAG_CLIENTE_NASCIMENTO(TagClienteNascimento.class),
	TAG_CLIENTE_CPF(TagClienteCPF.class),
	TAG_CLIENTE_RG(TagClienteRG.class),
	
	TAG_TIPO_EVENTO(TagTipoEvento.class),
	
	TAG_LOCACAO_ITEM_EM_LINHA(TagLocacaoItemEmLinha.class),
	TAG_LOCACAO_ITEM_EM_TABELA(TagLocacaoItemEmTabela.class);
	
	private Class<?> classeResposavel;
	
	private EnumModeloContratoTag(Class<?> cr) {
		this.classeResposavel = cr;
	}
	
	public Class<?> getClasseResposavel() {
		return classeResposavel;
	}

	public void setClasseResposavel(Class<?> classeResposavel) {
		this.classeResposavel = classeResposavel;
	}

	public static List<EnumModeloContratoTag> getOrdenedListByDescricao() {
		return Arrays.asList(EnumModeloContratoTag.values()).stream().sorted((e1, e2) -> e1.name().compareTo(e2.name())).collect(Collectors.toList());
	}

}
