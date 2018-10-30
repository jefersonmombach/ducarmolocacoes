package tech.jefersonms.ducarmolocacoes.service.util.contrato;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.service.util.DateUtil;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

public abstract class Tag {

	private EnumModeloContratoTag tag;
	private Tag next;

	public void setNext(Tag tcp) {
		if (next == null) {
			next = tcp;
		} else {
			next.setNext(tcp);
		}
	}

	public String getValue(String tagName, Locacao locacao)
			throws Exception {
		if (this.tag.name().compareToIgnoreCase(tagName) == 0) {
			return this.getFormattedValue(tagName, locacao);
		} else {
			if (next == null) {
				throw new Exception(
						"Não foi encontrado processador para a TAG (" + tagName
								+ ").");
			} else {
				return next.getValue(tagName, locacao);
			}
		}
	}

	protected abstract String getFormattedValue(String tagName, Locacao locacao);

	public EnumModeloContratoTag getTag() {
		return tag;
	}

	public void setTag(EnumModeloContratoTag tag) {
		this.tag = tag;
	}

	protected String formatNumber2c(BigDecimal valor) {
		NumberFormat nf = NumberFormat
				.getNumberInstance(new Locale("pt", "BR"));
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###.00");
		String output = df.format(valor);
		return output;
	}

	protected String formatDate(Date data) {
		return DateUtil.formatDateDDMMYYYY(data);
	}
	
	protected String formatDateSemiExtended(Date data) {
		return DateUtil.formatDateSemiExtended(data);
	}

}
