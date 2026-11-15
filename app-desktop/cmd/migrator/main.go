package main

import (
	"context"
	"flag"
	"fmt"
	"log"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
)

func main() {
	dbPath := flag.String("db", "ducarmolocacoes.db", "Caminho do banco SQLite alvo")
	seedData := flag.Bool("seed", false, "Popular base com dados demonstrativos para testes")
	flag.Parse()

	db, err := sqlite.Open(*dbPath)
	if err != nil {
		log.Fatalf("Falha ao abrir SQLite: %v", err)
	}
	defer db.Close()

	ctx := context.Background()

	if *seedData {
		fmt.Println("Inserindo dados de teste demonstrativos...")
		cliRepo := sqlite.NewClienteRepository(db)
		prodRepo := sqlite.NewProdutoRepository(db)
		locRepo := sqlite.NewLocacaoRepository(db)

		// Cria cliente demonstrativo
		cli, err := cliRepo.Criar(ctx, &domain.Cliente{
			Nome:      "Juliana Cristina Marcondes",
			CPF:       "999.888.777-66",
			RG:        "1122334-5",
			Telefone1: "(41) 99123-4567",
			Endereco:  "Rua XV de Novembro, 250",
			Bairro:    "Centro",
			Cidade:    "Bocaiúva do Sul",
			CEP:       "83450-000",
		})
		if err != nil {
			log.Fatalf("Erro ao criar cliente: %v", err)
		}

		// Cria produto
		prod, err := prodRepo.Criar(ctx, &domain.Produto{
			Descricao: "Vestido Sereia com Cauda Bordada",
			ValorBase: domain.DeReais(320.0),
			Ativo:     true,
		})
		if err != nil {
			log.Fatalf("Erro ao criar produto: %v", err)
		}

		// Cria locação
		ev := time.Now().AddDate(0, 0, 14)
		loc := &domain.Locacao{
			ClienteID:       cli.ID,
			DataContratacao: time.Now(),
			DataEvento:      ev,
			DataEntrPrev:    ev.AddDate(0, 0, -1),
			DataDevPrev:     ev.AddDate(0, 0, 2),
			ValorTotal:      prod.ValorBase,
			ValorSinal:      domain.DeReais(64.0), // 20%
			ValorSaldo:      domain.DeReais(256.0),
			Situacao:        domain.SituacaoNovo,
			Itens: []domain.ItemLocacao{
				{
					ProdutoID:  prod.ID,
					Quantidade: 1,
					ValorUnit:  prod.ValorBase,
					ValorTotal: prod.ValorBase,
				},
			},
		}

		salva, err := locRepo.Criar(ctx, loc)
		if err != nil {
			log.Fatalf("Erro ao criar locação de teste: %v", err)
		}

		fmt.Printf("Migração/Seed concluída! Locação #%d criada com sucesso no banco %s.\n", salva.ID, *dbPath)
	} else {
		fmt.Printf("Banco de dados SQLite verificado com sucesso em: %s\n", *dbPath)
	}
}
