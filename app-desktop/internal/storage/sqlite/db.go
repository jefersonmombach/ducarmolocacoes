package sqlite

import (
	"database/sql"
	"fmt"
	"os"
	"path/filepath"

	_ "modernc.org/sqlite"
)

type DB struct {
	*sql.DB
}

// Open inicializa a conexão com o banco de dados SQLite local, garantindo PRAGMA e DDL.
func Open(caminho string) (*DB, error) {
	if caminho != ":memory:" {
		dir := filepath.Dir(caminho)
		if dir != "." && dir != "" {
			if err := os.MkdirAll(dir, 0755); err != nil {
				return nil, fmt.Errorf("falha ao criar pasta do banco: %w", err)
			}
		}
	}

	db, err := sql.Open("sqlite", caminho)
	if err != nil {
		return nil, fmt.Errorf("falha ao abrir sqlite: %w", err)
	}

	// Pragmas recomendados para concorrência desktop rápida e segurança referencial
	pragmas := []string{
		"PRAGMA foreign_keys = ON;",
		"PRAGMA journal_mode = WAL;",
		"PRAGMA synchronous = NORMAL;",
		"PRAGMA busy_timeout = 5000;",
	}
	for _, p := range pragmas {
		if _, err := db.Exec(p); err != nil {
			return nil, fmt.Errorf("falha ao configurar pragma '%s': %w", p, err)
		}
	}

	// Executa a migração DDL do schema
	if _, err := db.Exec(SchemaDDL); err != nil {
		return nil, fmt.Errorf("falha ao aplicar schema DDL: %w", err)
	}

	// Insere seeds iniciais caso necessário
	if _, err := db.Exec(SeedsSQL); err != nil {
		return nil, fmt.Errorf("falha ao inserir seeds: %w", err)
	}

	return &DB{db}, nil
}
