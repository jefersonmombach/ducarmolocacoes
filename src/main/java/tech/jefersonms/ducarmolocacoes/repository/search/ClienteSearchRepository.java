package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.Cliente;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Cliente entity.
 */
public interface ClienteSearchRepository extends ElasticsearchRepository<Cliente, Long> {
}
