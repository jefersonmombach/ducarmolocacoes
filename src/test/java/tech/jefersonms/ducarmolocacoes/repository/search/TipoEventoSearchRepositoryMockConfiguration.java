package tech.jefersonms.ducarmolocacoes.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of TipoEventoSearchRepository to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class TipoEventoSearchRepositoryMockConfiguration {

    @MockBean
    private TipoEventoSearchRepository mockTipoEventoSearchRepository;

}
