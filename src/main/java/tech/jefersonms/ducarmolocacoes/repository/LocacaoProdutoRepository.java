package tech.jefersonms.ducarmolocacoes.repository;

import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the LocacaoProduto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocacaoProdutoRepository extends JpaRepository<LocacaoProduto, Long> {
    List<LocacaoProduto> findByLocacao(Locacao locacao);
}
