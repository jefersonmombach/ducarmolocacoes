package agenda

import (
	"context"
	"testing"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type mockAgendaRepo struct {
	conflitos []domain.Locacao
}

func (m *mockAgendaRepo) BuscarLocacoesConflitantes(ctx context.Context, produtoID int64, inicio, fim time.Time, locacaoIgnoradaID int64) ([]domain.Locacao, error) {
	return m.conflitos, nil
}

func TestAgendaCheckerIntervalo(t *testing.T) {
	evento := time.Date(2026, 11, 20, 15, 0, 0, 0, time.UTC)
	ini, fim := IntervaloProtecao(evento)

	esperadoIni := time.Date(2026, 11, 17, 0, 0, 0, 0, time.UTC)
	if !ini.Equal(esperadoIni) {
		t.Fatalf("esperado inicio %v, obtido %v", esperadoIni, ini)
	}

	esperadoFimDia := 24
	if fim.Day() != esperadoFimDia {
		t.Fatalf("esperado fim no dia %d, obtido %d", esperadoFimDia, fim.Day())
	}
}

func TestAgendaCheckerComConflito(t *testing.T) {
	repo := &mockAgendaRepo{
		conflitos: []domain.Locacao{
			{
				ID:         42,
				DataEvento: time.Date(2026, 11, 21, 0, 0, 0, 0, time.UTC),
			},
		},
	}
	checker := NewChecker(repo)

	locacao := &domain.Locacao{
		ID:         10,
		DataEvento: time.Date(2026, 11, 20, 0, 0, 0, 0, time.UTC),
		Itens: []domain.ItemLocacao{
			{ProdutoID: 5},
		},
	}

	err := checker.ValidarDisponibilidade(context.Background(), locacao)
	if err == nil {
		t.Fatalf("esperado erro de conflito de agenda, mas retornou nil")
	}
}

func TestAgendaCheckerSemConflito(t *testing.T) {
	repo := &mockAgendaRepo{
		conflitos: nil,
	}
	checker := NewChecker(repo)

	locacao := &domain.Locacao{
		ID:         10,
		DataEvento: time.Date(2026, 11, 20, 0, 0, 0, 0, time.UTC),
		Itens: []domain.ItemLocacao{
			{ProdutoID: 5},
		},
	}

	err := checker.ValidarDisponibilidade(context.Background(), locacao)
	if err != nil {
		t.Fatalf("esperado sucesso sem conflito, obtido erro: %v", err)
	}
}
