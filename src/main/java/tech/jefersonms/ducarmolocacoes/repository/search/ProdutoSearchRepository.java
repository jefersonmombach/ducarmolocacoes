package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.Produto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Produto entity.
 */
public interface ProdutoSearchRepository extends ElasticsearchRepository<Produto, Long> {
}
