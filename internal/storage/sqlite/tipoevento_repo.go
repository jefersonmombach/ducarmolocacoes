package sqlite

import (
	"context"
	"fmt"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type TipoEventoRepository struct {
	db *DB
}

func NewTipoEventoRepository(db *DB) *TipoEventoRepository {
	return &TipoEventoRepository{db: db}
}

func (r *TipoEventoRepository) Listar(ctx context.Context) ([]domain.TipoEvento, error) {
	rows, err := r.db.QueryContext(ctx, "SELECT id, descricao FROM tipo_eventos ORDER BY descricao ASC")
	if err != nil {
		return nil, fmt.Errorf("falha ao listar tipos de eventos: %w", err)
	}
	defer rows.Close()

	var lista []domain.TipoEvento
	for rows.Next() {
		var te domain.TipoEvento
		if err := rows.Scan(&te.ID, &te.Descricao); err != nil {
			return nil, err
		}
		lista = append(lista, te)
	}
	return lista, nil
}
