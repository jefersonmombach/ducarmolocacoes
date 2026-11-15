package agenda

import (
	"context"
	"fmt"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

// Repository define a interface de consulta de conflitos na base de dados.
type Repository interface {
	BuscarLocacoesConflitantes(ctx context.Context, produtoID int64, inicio, fim time.Time, locacaoIgnoradaID int64) ([]domain.Locacao, error)
}

// Checker valida a disponibilidade de produtos em função da data do evento.
type Checker struct {
	repo Repository
}

func NewChecker(repo Repository) *Checker {
	return &Checker{repo: repo}
}

// IntervaloProtecao retorna [dataEvento - 3 dias, dataEvento + 4 dias] normalizado em início e fim do dia.
func IntervaloProtecao(dataEvento time.Time) (inicio time.Time, fim time.Time) {
	ano, mes, dia := dataEvento.Date()
	base := time.Date(ano, mes, dia, 0, 0, 0, 0, dataEvento.Location())
	inicio = base.AddDate(0, 0, -3)
	fim = base.AddDate(0, 0, 4).Add(23*time.Hour + 59*time.Minute + 59*time.Second)
	return inicio, fim
}

// ValidarDisponibilidade verifica se os itens da locação estão livres na janela do evento.
func (c *Checker) ValidarDisponibilidade(ctx context.Context, locacao *domain.Locacao) error {
	inicio, fim := IntervaloProtecao(locacao.DataEvento)

	for _, item := range locacao.Itens {
		conflitos, err := c.repo.BuscarLocacoesConflitantes(ctx, item.ProdutoID, inicio, fim, locacao.ID)
		if err != nil {
			return fmt.Errorf("erro ao verificar conflitos de agenda: %w", err)
		}
		if len(conflitos) > 0 {
			conf := conflitos[0]
			return fmt.Errorf("%w: traje #%d conflitante com locação #%d (evento em %s)",
				domain.ErrConflitoAgenda,
				item.ProdutoID,
				conf.ID,
				conf.DataEvento.Format("02/01/2006"),
			)
		}
	}

	return nil
}
