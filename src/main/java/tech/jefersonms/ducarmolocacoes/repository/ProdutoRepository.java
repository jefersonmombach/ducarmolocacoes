package tech.jefersonms.ducarmolocacoes.repository;

import tech.jefersonms.ducarmolocacoes.domain.Produto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Produto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

}
