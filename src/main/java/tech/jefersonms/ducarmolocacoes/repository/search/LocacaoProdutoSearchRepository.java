package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the LocacaoProduto entity.
 */
public interface LocacaoProdutoSearchRepository extends ElasticsearchRepository<LocacaoProduto, Long> {
}
