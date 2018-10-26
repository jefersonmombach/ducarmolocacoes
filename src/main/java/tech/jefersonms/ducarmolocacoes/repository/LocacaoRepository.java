package tech.jefersonms.ducarmolocacoes.repository;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Locacao entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocacaoRepository extends JpaRepository<Locacao, Long> {

}
