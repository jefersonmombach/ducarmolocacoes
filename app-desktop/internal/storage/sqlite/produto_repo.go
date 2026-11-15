package sqlite

import (
	"context"
	"database/sql"
	"fmt"
	"strings"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type ProdutoRepository struct {
	db *DB
}

func NewProdutoRepository(db *DB) *ProdutoRepository {
	return &ProdutoRepository{db: db}
}

func (r *ProdutoRepository) Criar(ctx context.Context, p *domain.Produto) (*domain.Produto, error) {
	query := `
		INSERT INTO produtos (descricao, valor_base, ativo)
		VALUES (?, ?, ?)
	`
	ativoInt := 0
	if p.Ativo {
		ativoInt = 1
	}
	res, err := r.db.ExecContext(ctx, query, p.Descricao, int64(p.ValorBase), ativoInt)
	if err != nil {
		return nil, fmt.Errorf("falha ao criar produto: %w", err)
	}

	id, err := res.LastInsertId()
	if err != nil {
		return nil, err
	}
	p.ID = id

	// FTS5
	_, _ = r.db.ExecContext(ctx, "INSERT INTO produtos_fts(rowid, descricao) VALUES (?, ?)", p.ID, p.Descricao)

	return p, nil
}

func (r *ProdutoRepository) Atualizar(ctx context.Context, p *domain.Produto) (*domain.Produto, error) {
	query := `
		UPDATE produtos SET descricao = ?, valor_base = ?, ativo = ? WHERE id = ?
	`
	ativoInt := 0
	if p.Ativo {
		ativoInt = 1
	}
	_, err := r.db.ExecContext(ctx, query, p.Descricao, int64(p.ValorBase), ativoInt, p.ID)
	if err != nil {
		return nil, fmt.Errorf("falha ao atualizar produto: %w", err)
	}

	// FTS5
	_, _ = r.db.ExecContext(ctx, "DELETE FROM produtos_fts WHERE rowid = ?", p.ID)
	_, _ = r.db.ExecContext(ctx, "INSERT INTO produtos_fts(rowid, descricao) VALUES (?, ?)", p.ID, p.Descricao)

	return p, nil
}

func (r *ProdutoRepository) ObterPorID(ctx context.Context, id int64) (*domain.Produto, error) {
	query := `SELECT id, descricao, valor_base, ativo FROM produtos WHERE id = ?`
	var p domain.Produto
	var valorInt int64
	var ativoInt int
	if err := r.db.QueryRowContext(ctx, query, id).Scan(&p.ID, &p.Descricao, &valorInt, &ativoInt); err != nil {
		return nil, err
	}
	p.ValorBase = domain.Dinheiro(valorInt)
	p.Ativo = (ativoInt == 1)
	return &p, nil
}

func (r *ProdutoRepository) Listar(ctx context.Context, apenasAtivos bool, busca string) ([]domain.Produto, error) {
	var rows *sql.Rows
	var err error

	busca = strings.TrimSpace(busca)
	if busca != "" {
		query := `
			SELECT p.id, p.descricao, p.valor_base, p.ativo
			FROM produtos p
			WHERE (? = 0 OR p.ativo = 1)
			  AND (p.id IN (SELECT rowid FROM produtos_fts WHERE produtos_fts MATCH ?) OR p.descricao LIKE ?)
			ORDER BY p.descricao ASC
		`
		filtroAtivo := 0
		if apenasAtivos {
			filtroAtivo = 1
		}
		matchQuery := busca + "*"
		wild := "%" + busca + "%"
		rows, err = r.db.QueryContext(ctx, query, filtroAtivo, matchQuery, wild)
	} else {
		query := `
			SELECT id, descricao, valor_base, ativo
			FROM produtos
			WHERE (? = 0 OR ativo = 1)
			ORDER BY descricao ASC
		`
		filtroAtivo := 0
		if apenasAtivos {
			filtroAtivo = 1
		}
		rows, err = r.db.QueryContext(ctx, query, filtroAtivo)
	}

	if err != nil {
		return nil, fmt.Errorf("falha ao listar produtos: %w", err)
	}
	defer rows.Close()

	var lista []domain.Produto
	for rows.Next() {
		var p domain.Produto
		var valorInt int64
		var ativoInt int
		if err := rows.Scan(&p.ID, &p.Descricao, &valorInt, &ativoInt); err != nil {
			return nil, err
		}
		p.ValorBase = domain.Dinheiro(valorInt)
		p.Ativo = (ativoInt == 1)
		lista = append(lista, p)
	}
	return lista, nil
}
