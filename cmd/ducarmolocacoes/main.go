package main

import (
	"flag"
	"fmt"
	"log"
	"os"
	"path/filepath"

	"tech.jefersonms/ducarmolocacoes/internal/core/agenda"
	"tech.jefersonms/ducarmolocacoes/internal/core/locacao"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
	"tech.jefersonms/ducarmolocacoes/internal/ui"
)

func main() {
	dbPathFlag := flag.String("db", "ducarmolocacoes.db", "Caminho do arquivo de banco de dados SQLite")
	flag.Parse()

	dbPath := *dbPathFlag
	if dbPath == "ducarmolocacoes.db" {
		homeDir, err := os.UserHomeDir()
		if err == nil {
			appDir := filepath.Join(homeDir, ".ducarmolocacoes")
			_ = os.MkdirAll(appDir, 0755)
			dbPath = filepath.Join(appDir, "ducarmolocacoes.db")
		}
	}

	fmt.Printf("Iniciando Du Carmo Locações Desktop...\n")
	fmt.Printf("Banco de dados local: %s\n", dbPath)

	db, err := sqlite.Open(dbPath)
	if err != nil {
		log.Fatalf("Falha crítica ao inicializar banco de dados SQLite: %v", err)
	}
	defer db.Close()

	locRepo := sqlite.NewLocacaoRepository(db)
	cliRepo := sqlite.NewClienteRepository(db)
	prodRepo := sqlite.NewProdutoRepository(db)
	checker := agenda.NewChecker(locRepo)
	svc := locacao.NewService(locRepo, checker)

	ui.RunApp(svc, cliRepo, prodRepo)
}
