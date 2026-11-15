package locacao

import (
	"context"
	"testing"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/agenda"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type mockLocacaoRepo struct {
	armazenadas map[int64]*domain.Locacao
	nextID      int64
}

func newMockLocacaoRepo() *mockLocacaoRepo {
	return &mockLocacaoRepo{
		armazenadas: make(map[int64]*domain.Locacao),
		nextID:      1,
	}
}

func (m *mockLocacaoRepo) Criar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error) {
	loc.ID = m.nextID
	m.nextID++
	m.armazenadas[loc.ID] = loc
	return loc, nil
}

func (m *mockLocacaoRepo) Atualizar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error) {
	m.armazenadas[loc.ID] = loc
	return loc, nil
}

func (m *mockLocacaoRepo) ObterPorID(ctx context.Context, id int64) (*domain.Locacao, error) {
	loc, ok := m.armazenadas[id]
	if !ok {
		return nil, domain.ErrTransicaoInvalida
	}
	return loc, nil
}

func (m *mockLocacaoRepo) Listar(ctx context.Context, situacao *domain.SituacaoLocacao, busca string) ([]domain.Locacao, error) {
	var lista []domain.Locacao
	for _, l := range m.armazenadas {
		if situacao == nil || l.Situacao == *situacao {
			lista = append(lista, *l)
		}
	}
	return lista, nil
}

func (m *mockLocacaoRepo) Excluir(ctx context.Context, id int64) error {
	delete(m.armazenadas, id)
	return nil
}

type mockAgendaRepo struct{}

func (m *mockAgendaRepo) BuscarLocacoesConflitantes(ctx context.Context, produtoID int64, inicio, fim time.Time, locacaoIgnoradaID int64) ([]domain.Locacao, error) {
	return nil, nil // sem conflito
}

func TestLocacaoServiceFluxoCompleto(t *testing.T) {
	repo := newMockLocacaoRepo()
	checker := agenda.NewChecker(&mockAgendaRepo{})
	svc := NewService(repo, checker)

	ctx := context.Background()
	evento := time.Now().AddDate(0, 0, 15)

	loc := &domain.Locacao{
		ClienteID:    1,
		DataEvento:   evento,
		DataEntrPrev: evento.AddDate(0, 0, -1),
		DataDevPrev:  evento.AddDate(0, 0, 2),
		ValorSinal:   domain.DeReais(40.0), // 20% de 200
		Itens: []domain.ItemLocacao{
			{
				ProdutoID:  1,
				Quantidade: 1,
				ValorUnit:  domain.DeReais(200.0),
			},
		},
	}

	// 1. Salvar Locação
	salva, err := svc.Salvar(ctx, loc)
	if err != nil {
		t.Fatalf("falha ao salvar locacao: %v", err)
	}
	if salva.ID == 0 {
		t.Fatalf("esperado ID gerado")
	}
	if salva.Situacao != domain.SituacaoNovo {
		t.Fatalf("esperado situacao Novo, obtido %s", salva.Situacao)
	}
	if salva.ValorSaldo != domain.DeReais(160.0) {
		t.Fatalf("esperado saldo R$ 160.00, obtido: %s", salva.ValorSaldo.FormatBR())
	}

	// 2. Emitir Contrato (0 -> 1)
	emitida, err := svc.EmitirContrato(ctx, salva.ID)
	if err != nil {
		t.Fatalf("falha ao emitir contrato: %v", err)
	}
	if emitida.Situacao != domain.SituacaoContratoEmitido {
		t.Fatalf("esperado situacao Contrato Emitido")
	}

	// 3. Tentar entregar com saldo devedor pendente -> deve falhar
	_, err = svc.EntregarTrajes(ctx, salva.ID)
	if err == nil {
		t.Fatalf("esperado erro ao tentar entregar trajes com saldo pendente")
	}

	// 4. Quitar saldo restante e entregar (1 -> 2)
	emitida.ValorAdiantado = domain.DeReais(160.0)
	emitida.RecalcularTotais()
	_, _ = repo.Atualizar(ctx, emitida)

	entregue, err := svc.EntregarTrajes(ctx, salva.ID)
	if err != nil {
		t.Fatalf("falha ao entregar trajes quitados: %v", err)
	}
	if entregue.Situacao != domain.SituacaoTrajesEntregues {
		t.Fatalf("esperado situacao Trajes Entregues")
	}
	if entregue.DataEntrReal == nil {
		t.Fatalf("esperado DataEntrReal preenchida")
	}

	// 5. Devolver Trajes (2 -> 3)
	devolvido, err := svc.DevolverTrajes(ctx, salva.ID)
	if err != nil {
		t.Fatalf("falha ao devolver trajes: %v", err)
	}
	if devolvido.Situacao != domain.SituacaoTrajesDevolvidos {
		t.Fatalf("esperado situacao Trajes Devolvidos")
	}
	if devolvido.DataDevReal == nil {
		t.Fatalf("esperado DataDevReal preenchida")
	}
}
