package sqlite

import (
	"context"
	"database/sql"
	"fmt"
	"strings"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type ClienteRepository struct {
	db *DB
}

func NewClienteRepository(db *DB) *ClienteRepository {
	return &ClienteRepository{db: db}
}

func (r *ClienteRepository) Criar(ctx context.Context, c *domain.Cliente) (*domain.Cliente, error) {
	query := `
		INSERT INTO clientes (nome, cpf, rg, telefone1, telefone2, endereco, bairro, cidade, cep)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
	`
	res, err := r.db.ExecContext(ctx, query,
		c.Nome, c.CPF, c.RG, c.Telefone1, c.Telefone2, c.Endereco, c.Bairro, c.Cidade, c.CEP,
	)
	if err != nil {
		return nil, fmt.Errorf("falha ao inserir cliente: %w", err)
	}

	id, err := res.LastInsertId()
	if err != nil {
		return nil, err
	}
	c.ID = id

	// Sincroniza FTS5
	_, _ = r.db.ExecContext(ctx, "INSERT INTO clientes_fts(rowid, nome, cpf, telefone1) VALUES (?, ?, ?, ?)",
		c.ID, c.Nome, c.CPF, c.Telefone1)

	return c, nil
}

func (r *ClienteRepository) Atualizar(ctx context.Context, c *domain.Cliente) (*domain.Cliente, error) {
	query := `
		UPDATE clientes 
		SET nome = ?, cpf = ?, rg = ?, telefone1 = ?, telefone2 = ?, endereco = ?, bairro = ?, cidade = ?, cep = ?
		WHERE id = ?
	`
	_, err := r.db.ExecContext(ctx, query,
		c.Nome, c.CPF, c.RG, c.Telefone1, c.Telefone2, c.Endereco, c.Bairro, c.Cidade, c.CEP, c.ID,
	)
	if err != nil {
		return nil, fmt.Errorf("falha ao atualizar cliente: %w", err)
	}

	// Atualiza FTS5
	_, _ = r.db.ExecContext(ctx, "DELETE FROM clientes_fts WHERE rowid = ?", c.ID)
	_, _ = r.db.ExecContext(ctx, "INSERT INTO clientes_fts(rowid, nome, cpf, telefone1) VALUES (?, ?, ?, ?)",
		c.ID, c.Nome, c.CPF, c.Telefone1)

	return c, nil
}

func (r *ClienteRepository) ObterPorID(ctx context.Context, id int64) (*domain.Cliente, error) {
	query := `
		SELECT id, nome, cpf, rg, telefone1, telefone2, endereco, bairro, cidade, cep, criado_em
		FROM clientes WHERE id = ?
	`
	var c domain.Cliente
	var rg, tel2, end, bai, cid, cep sql.NullString
	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&c.ID, &c.Nome, &c.CPF, &rg, &c.Telefone1, &tel2, &end, &bai, &cid, &cep, &c.CriadoEm,
	)
	if err != nil {
		return nil, err
	}
	c.RG = rg.String
	c.Telefone2 = tel2.String
	c.Endereco = end.String
	c.Bairro = bai.String
	c.Cidade = cid.String
	c.CEP = cep.String
	return &c, nil
}

func (r *ClienteRepository) Listar(ctx context.Context, busca string) ([]domain.Cliente, error) {
	var rows *sql.Rows
	var err error

	busca = strings.TrimSpace(busca)
	if busca != "" {
		query := `
			SELECT c.id, c.nome, c.cpf, c.rg, c.telefone1, c.telefone2, c.endereco, c.bairro, c.cidade, c.cep, c.criado_em
			FROM clientes c
			WHERE c.id IN (SELECT rowid FROM clientes_fts WHERE clientes_fts MATCH ?)
			   OR c.nome LIKE ? OR c.cpf LIKE ? OR c.telefone1 LIKE ?
			ORDER BY c.nome ASC LIMIT 100
		`
		wild := "%" + busca + "%"
		matchQuery := busca + "*"
		rows, err = r.db.QueryContext(ctx, query, matchQuery, wild, wild, wild)
	} else {
		query := `
			SELECT id, nome, cpf, rg, telefone1, telefone2, endereco, bairro, cidade, cep, criado_em
			FROM clientes ORDER BY nome ASC LIMIT 100
		`
		rows, err = r.db.QueryContext(ctx, query)
	}

	if err != nil {
		return nil, fmt.Errorf("falha ao listar clientes: %w", err)
	}
	defer rows.Close()

	var lista []domain.Cliente
	for rows.Next() {
		var c domain.Cliente
		var rg, tel2, end, bai, cid, cep sql.NullString
		if err := rows.Scan(&c.ID, &c.Nome, &c.CPF, &rg, &c.Telefone1, &tel2, &end, &bai, &cid, &cep, &c.CriadoEm); err != nil {
			return nil, err
		}
		c.RG = rg.String
		c.Telefone2 = tel2.String
		c.Endereco = end.String
		c.Bairro = bai.String
		c.Cidade = cid.String
		c.CEP = cep.String
		lista = append(lista, c)
	}
	return lista, nil
}
