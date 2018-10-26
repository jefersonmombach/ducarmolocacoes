package tech.jefersonms.ducarmolocacoes.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of LocacaoProdutoSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class LocacaoProdutoSearchRepositoryMockConfiguration {

    @MockBean
    private LocacaoProdutoSearchRepository mockLocacaoProdutoSearchRepository;

}
