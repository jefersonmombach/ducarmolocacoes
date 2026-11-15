package sqlite

import (
	"context"
	"testing"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/agenda"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/core/locacao"
)

func TestSQLiteIntegrationFlow(t *testing.T) {
	// Usa banco SQLite em memória
	db, err := Open(":memory:")
	if err != nil {
		t.Fatalf("falha ao inicializar sqlite em memoria: %v", err)
	}
	defer db.Close()

	ctx := context.Background()

	// 1. Testa listagem de clientes (com seeds)
	cliRepo := NewClienteRepository(db)
	clientes, err := cliRepo.Listar(ctx, "")
	if err != nil {
		t.Fatalf("falha ao listar clientes: %v", err)
	}
	if len(clientes) < 2 {
		t.Fatalf("esperado ao menos 2 clientes seeded, obtido %d", len(clientes))
	}

	// 2. Testa busca FTS5 de clientes
	buscaFTS, err := cliRepo.Listar(ctx, "Fernanda")
	if err != nil {
		t.Fatalf("falha na busca FTS5: %v", err)
	}
	if len(buscaFTS) == 0 {
		t.Fatalf("esperado encontrar 'Fernanda' via FTS5")
	}

	// 3. Testa produtos
	prodRepo := NewProdutoRepository(db)
	produtos, err := prodRepo.Listar(ctx, true, "")
	if err != nil {
		t.Fatalf("falha ao listar produtos: %v", err)
	}
	if len(produtos) == 0 {
		t.Fatalf("esperado produtos seeded")
	}

	// 4. Testa persistência de locação completa com itens
	locRepo := NewLocacaoRepository(db)
	checker := agenda.NewChecker(locRepo)
	svc := locacao.NewService(locRepo, checker)

	evento := time.Date(2026, 12, 10, 0, 0, 0, 0, time.UTC)
	novaLoc := &domain.Locacao{
		ClienteID:    clientes[0].ID,
		DataEvento:   evento,
		DataEntrPrev: evento.AddDate(0, 0, -1),
		DataDevPrev:  evento.AddDate(0, 0, 2),
		ValorSinal:   domain.DeReais(70.0), // >= 20%
		Itens: []domain.ItemLocacao{
			{
				ProdutoID:  produtos[0].ID,
				Quantidade: 1,
				ValorUnit:  produtos[0].ValorBase,
			},
		},
	}

	criada, err := svc.Salvar(ctx, novaLoc)
	if err != nil {
		t.Fatalf("falha ao salvar locacao no sqlite: %v", err)
	}
	if criada.ID == 0 {
		t.Fatalf("esperado ID gerado para locacao")
	}

	// 5. Testa colisão de agenda: tentar reservar o mesmo produto na janela de 7 dias [evento-3, evento+4]
	eventoConflitante := evento.AddDate(0, 0, 2) // 2 dias depois do evento -> dentro de +4 dias!
	locConflitante := &domain.Locacao{
		ClienteID:    clientes[1].ID,
		DataEvento:   eventoConflitante,
		DataEntrPrev: eventoConflitante.AddDate(0, 0, -1),
		DataDevPrev:  eventoConflitante.AddDate(0, 0, 2),
		ValorSinal:   domain.DeReais(70.0),
		Itens: []domain.ItemLocacao{
			{
				ProdutoID:  produtos[0].ID,
				Quantidade: 1,
				ValorUnit:  produtos[0].ValorBase,
			},
		},
	}

	_, errConflito := svc.Salvar(ctx, locConflitante)
	if errConflito == nil {
		t.Fatalf("esperado erro de conflito de agenda no SQLite, mas salvou com sucesso")
	}

	// 6. Testar obtenção por ID e carregar todos os relacionamentos
	recuperada, err := locRepo.ObterPorID(ctx, criada.ID)
	if err != nil {
		t.Fatalf("falha ao recuperar locacao por id: %v", err)
	}
	if recuperada.Cliente == nil || recuperada.Cliente.Nome == "" {
		t.Fatalf("cliente nao foi carregado na locacao")
	}
	if len(recuperada.Itens) == 0 {
		t.Fatalf("itens da locacao nao foram carregados")
	}
}
