package tech.jefersonms.ducarmolocacoes.service.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String formatDateDDMMYYYY(Date data) {
		String pattern = "dd/MM/yyyy";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(data);
	}
	
	public static String formatDateSemiExtended(Date data) {
		String retorno = "";
		
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(data.getTime());
		
		retorno += c.get(Calendar.DAY_OF_MONTH) + " de " + defineMonth(c) + " de " + c.get(Calendar.YEAR);
		
		return retorno;
	}
	
	public static String defineMonth(Calendar c) {
		int mes = c.get(Calendar.MONTH);
		switch (mes) {
		case 0:
			return "Janeiro";
		case 1:
			return "Fevereiro";
		case 2:
			return "Março";
		case 3:
			return "Abril";
		case 4:
			return "Maio";
		case 5:
			return "Junho";
		case 6:
			return "Julho";
		case 7:
			return "Agosto";
		case 8:
			return "Setembro";
		case 9:
			return "Outubro";
		case 10:
			return "Novembro";
		case 11:
			return "Dezembro";
		default:
			return "Mês ("+mes+") Indefindo";
		}
	}

}
