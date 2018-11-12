package tech.jefersonms.ducarmolocacoes.repository;

import org.springframework.data.repository.query.Param;
import tech.jefersonms.ducarmolocacoes.domain.Locacao;
import tech.jefersonms.ducarmolocacoes.domain.LocacaoProduto;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Spring Data  repository for the LocacaoProduto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocacaoProdutoRepository extends JpaRepository<LocacaoProduto, Long> {
    List<LocacaoProduto> findByLocacao(Locacao locacao);
    @Query(name = "listAllLocacoes",
        value = "SELECT lp " +
            "  FROM LocacaoProduto lp " +
            " INNER JOIN lp.locacao l " +
            " WHERE l.dataEvento BETWEEN :dataIni AND :dataFim " +
            "   AND l.situacao not in (0,9,3) ")
    List<LocacaoProduto> listAllLocacoes(@Param("dataIni") LocalDate dataIni, @Param("dataFim") LocalDate dataFim);
}
