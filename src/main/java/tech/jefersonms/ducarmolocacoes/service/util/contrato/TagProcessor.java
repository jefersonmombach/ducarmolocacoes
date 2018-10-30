package tech.jefersonms.ducarmolocacoes.service.util.contrato;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;

public class TagProcessor {

    private final Logger log = LoggerFactory.getLogger(TagProcessor.class);

	private static TagProcessor instance;
	private Tag tag;
	
	private TagProcessor() throws InstantiationException, IllegalAccessException {
		boolean primero = true;
		for (EnumModeloContratoTag t : EnumModeloContratoTag.values()) {
			if (primero) {
				primero = false;
				tag = (Tag)t.getClasseResposavel().newInstance();
				tag.setTag(t);
			} else {
				Tag t1 = (Tag)t.getClasseResposavel().newInstance();
				t1.setTag(t);
				tag.setNext(t1);
			}
		}
	}
	
	public static TagProcessor getInstance () {
		if (instance == null) {
			try {
				instance = new TagProcessor();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return instance;
	}
	
	private String getValue(String tagName, Locacao locacao) throws Exception {
		return tag.getValue(tagName, locacao);
	}
	
	public String processarTags(String texto, Locacao locacao) {
		for (EnumModeloContratoTag tag : EnumModeloContratoTag.values()) {
		    log.debug("Processando Tag " + tag.name());
			if (texto.indexOf(tag.name()) > -1) {
				String txtSub;
				try {
					txtSub = this.getValue(tag.name(), locacao);
				} catch (Exception e) {
					e.printStackTrace();
					txtSub = "Erro ao fazer a conversão da TAG ("+tag.name()+").";
				}
				texto = texto.replace(tag.name(), txtSub);
			}
		}
		return texto;
	}
	
}
