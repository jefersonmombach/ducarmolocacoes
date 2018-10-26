package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Locacao entity.
 */
public interface LocacaoSearchRepository extends ElasticsearchRepository<Locacao, Long> {
}
