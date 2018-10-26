package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.TipoEvento;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the TipoEvento entity.
 */
public interface TipoEventoSearchRepository extends ElasticsearchRepository<TipoEvento, Long> {
}
