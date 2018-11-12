package tech.jefersonms.ducarmolocacoes.repository.search;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Spring Data Elasticsearch repository for the Locacao entity.
 */
public interface LocacaoSearchRepository extends ElasticsearchRepository<Locacao, Long> {

}
