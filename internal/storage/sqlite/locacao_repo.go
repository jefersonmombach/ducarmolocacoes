package sqlite

import (
	"context"
	"database/sql"
	"fmt"
	"strings"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

type LocacaoRepository struct {
	db *DB
}

func NewLocacaoRepository(db *DB) *LocacaoRepository {
	return &LocacaoRepository{db: db}
}

// Criar persiste uma nova locação e seus itens em transação atômica.
func (r *LocacaoRepository) Criar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error) {
	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		return nil, fmt.Errorf("falha ao iniciar transacao: %w", err)
	}
	defer tx.Rollback()

	query := `
		INSERT INTO locacoes (
			cliente_id, tipo_evento_id, data_contratacao, data_evento, 
			data_entr_prev, data_dev_prev, data_entr_real, data_dev_real,
			valor_total, valor_sinal, valor_adiantado, valor_saldo,
			situacao, html_contrato, observacoes
		) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
	`

	var teID *int64
	if loc.TipoEventoID != nil && *loc.TipoEventoID > 0 {
		teID = loc.TipoEventoID
	}

	res, err := tx.ExecContext(ctx, query,
		loc.ClienteID,
		teID,
		loc.DataContratacao.Format("2006-01-02"),
		loc.DataEvento.Format("2006-01-02"),
		loc.DataEntrPrev.Format("2006-01-02"),
		loc.DataDevPrev.Format("2006-01-02"),
		loc.DataEntrReal,
		loc.DataDevReal,
		int64(loc.ValorTotal),
		int64(loc.ValorSinal),
		int64(loc.ValorAdiantado),
		int64(loc.ValorSaldo),
		int(loc.Situacao),
		loc.HTMLContrato,
		loc.Observacoes,
	)
	if err != nil {
		return nil, fmt.Errorf("falha ao inserir locacao: %w", err)
	}

	id, err := res.LastInsertId()
	if err != nil {
		return nil, err
	}
	loc.ID = id

	// Inserir itens
	itemStmt, err := tx.PrepareContext(ctx, `
		INSERT INTO locacao_produtos (locacao_id, produto_id, quantidade, valor_unit, valor_total)
		VALUES (?, ?, ?, ?, ?)
	`)
	if err != nil {
		return nil, fmt.Errorf("falha ao preparar insert de itens: %w", err)
	}
	defer itemStmt.Close()

	for i := range loc.Itens {
		it := &loc.Itens[i]
		it.LocacaoID = id
		itRes, err := itemStmt.ExecContext(ctx, id, it.ProdutoID, it.Quantidade, int64(it.ValorUnit), int64(it.ValorTotal))
		if err != nil {
			return nil, fmt.Errorf("falha ao inserir item da locacao: %w", err)
		}
		it.ID, _ = itRes.LastInsertId()
	}

	if err := tx.Commit(); err != nil {
		return nil, fmt.Errorf("falha ao comitar locacao: %w", err)
	}

	return loc, nil
}

// Atualizar atualiza a locação e reconcilia os itens na transação.
func (r *LocacaoRepository) Atualizar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error) {
	tx, err := r.db.BeginTx(ctx, nil)
	if err != nil {
		return nil, fmt.Errorf("falha ao iniciar transacao: %w", err)
	}
	defer tx.Rollback()

	query := `
		UPDATE locacoes SET
			cliente_id = ?, tipo_evento_id = ?, data_contratacao = ?, data_evento = ?,
			data_entr_prev = ?, data_dev_prev = ?, data_entr_real = ?, data_dev_real = ?,
			valor_total = ?, valor_sinal = ?, valor_adiantado = ?, valor_saldo = ?,
			situacao = ?, html_contrato = ?, observacoes = ?
		WHERE id = ?
	`

	var teID *int64
	if loc.TipoEventoID != nil && *loc.TipoEventoID > 0 {
		teID = loc.TipoEventoID
	}

	_, err = tx.ExecContext(ctx, query,
		loc.ClienteID,
		teID,
		loc.DataContratacao.Format("2006-01-02"),
		loc.DataEvento.Format("2006-01-02"),
		loc.DataEntrPrev.Format("2006-01-02"),
		loc.DataDevPrev.Format("2006-01-02"),
		loc.DataEntrReal,
		loc.DataDevReal,
		int64(loc.ValorTotal),
		int64(loc.ValorSinal),
		int64(loc.ValorAdiantado),
		int64(loc.ValorSaldo),
		int(loc.Situacao),
		loc.HTMLContrato,
		loc.Observacoes,
		loc.ID,
	)
	if err != nil {
		return nil, fmt.Errorf("falha ao atualizar locacao: %w", err)
	}

	// Limpa itens antigos e reinsere atualizados
	if _, err := tx.ExecContext(ctx, "DELETE FROM locacao_produtos WHERE locacao_id = ?", loc.ID); err != nil {
		return nil, fmt.Errorf("falha ao remover itens antigos: %w", err)
	}

	itemStmt, err := tx.PrepareContext(ctx, `
		INSERT INTO locacao_produtos (locacao_id, produto_id, quantidade, valor_unit, valor_total)
		VALUES (?, ?, ?, ?, ?)
	`)
	if err != nil {
		return nil, err
	}
	defer itemStmt.Close()

	for i := range loc.Itens {
		it := &loc.Itens[i]
		it.LocacaoID = loc.ID
		itRes, err := itemStmt.ExecContext(ctx, loc.ID, it.ProdutoID, it.Quantidade, int64(it.ValorUnit), int64(it.ValorTotal))
		if err != nil {
			return nil, fmt.Errorf("falha ao reinserir item da locacao: %w", err)
		}
		it.ID, _ = itRes.LastInsertId()
	}

	if err := tx.Commit(); err != nil {
		return nil, fmt.Errorf("falha ao comitar atualizacao da locacao: %w", err)
	}

	return loc, nil
}

// ObterPorID busca uma locação com seus dados de cliente, tipo de evento e todos os itens.
func (r *LocacaoRepository) ObterPorID(ctx context.Context, id int64) (*domain.Locacao, error) {
	query := `
		SELECT 
			l.id, l.cliente_id, l.tipo_evento_id, l.data_contratacao, l.data_evento,
			l.data_entr_prev, l.data_dev_prev, l.data_entr_real, l.data_dev_real,
			l.valor_total, l.valor_sinal, l.valor_adiantado, l.valor_saldo,
			l.situacao, l.html_contrato, l.observacoes, l.criado_em,
			c.id, c.nome, c.cpf, c.rg, c.telefone1, c.telefone2, c.endereco, c.bairro, c.cidade, c.cep,
			te.id, te.descricao
		FROM locacoes l
		JOIN clientes c ON l.cliente_id = c.id
		LEFT JOIN tipo_eventos te ON l.tipo_evento_id = te.id
		WHERE l.id = ?
	`

	var loc domain.Locacao
	var cli domain.Cliente
	var teID sql.NullInt64
	var teDesc sql.NullString
	var dataContrStr, dataEvStr, dataEntrPrevStr, dataDevPrevStr string
	var dataEntrReal, dataDevReal sql.NullTime
	var total, sinal, adiantado, saldo int64
	var sit int
	var htmlContrato, observacoes sql.NullString
	var cliRG, cliTel2, cliEnd, cliBairro, cliCidade, cliCEP sql.NullString

	err := r.db.QueryRowContext(ctx, query, id).Scan(
		&loc.ID, &loc.ClienteID, &teID, &dataContrStr, &dataEvStr,
		&dataEntrPrevStr, &dataDevPrevStr, &dataEntrReal, &dataDevReal,
		&total, &sinal, &adiantado, &saldo,
		&sit, &htmlContrato, &observacoes, &loc.CriadoEm,
		&cli.ID, &cli.Nome, &cli.CPF, &cliRG, &cli.Telefone1, &cliTel2, &cliEnd, &cliBairro, &cliCidade, &cliCEP,
		&teID, &teDesc,
	)
	if err != nil {
		return nil, fmt.Errorf("locacao #%d nao encontrada: %w", id, err)
	}

	loc.DataContratacao, _ = time.Parse("2006-01-02", dataContrStr)
	loc.DataEvento, _ = time.Parse("2006-01-02", dataEvStr)
	loc.DataEntrPrev, _ = time.Parse("2006-01-02", dataEntrPrevStr)
	loc.DataDevPrev, _ = time.Parse("2006-01-02", dataDevPrevStr)
	if dataEntrReal.Valid {
		loc.DataEntrReal = &dataEntrReal.Time
	}
	if dataDevReal.Valid {
		loc.DataDevReal = &dataDevReal.Time
	}

	loc.ValorTotal = domain.Dinheiro(total)
	loc.ValorSinal = domain.Dinheiro(sinal)
	loc.ValorAdiantado = domain.Dinheiro(adiantado)
	loc.ValorSaldo = domain.Dinheiro(saldo)
	loc.Situacao = domain.SituacaoLocacao(sit)
	loc.HTMLContrato = htmlContrato.String
	loc.Observacoes = observacoes.String

	cli.RG = cliRG.String
	cli.Telefone2 = cliTel2.String
	cli.Endereco = cliEnd.String
	cli.Bairro = cliBairro.String
	cli.Cidade = cliCidade.String
	cli.CEP = cliCEP.String
	loc.Cliente = &cli

	if teDesc.Valid {
		loc.TipoEvento = &domain.TipoEvento{
			ID:        teID.Int64,
			Descricao: teDesc.String,
		}
		loc.TipoEventoID = &teID.Int64
	}

	// Carrega itens da locação
	itensQuery := `
		SELECT lp.id, lp.locacao_id, lp.produto_id, lp.quantidade, lp.valor_unit, lp.valor_total,
		       p.descricao, p.valor_base, p.ativo
		FROM locacao_produtos lp
		JOIN produtos p ON lp.produto_id = p.id
		WHERE lp.locacao_id = ?
		ORDER BY lp.id ASC
	`
	rows, err := r.db.QueryContext(ctx, itensQuery, id)
	if err != nil {
		return nil, fmt.Errorf("falha ao carregar itens da locacao: %w", err)
	}
	defer rows.Close()

	for rows.Next() {
		var it domain.ItemLocacao
		var prod domain.Produto
		var vu, vt, vb int64
		var prodAtivo int
		if err := rows.Scan(
			&it.ID, &it.LocacaoID, &it.ProdutoID, &it.Quantidade, &vu, &vt,
			&prod.Descricao, &vb, &prodAtivo,
		); err != nil {
			return nil, err
		}
		it.ValorUnit = domain.Dinheiro(vu)
		it.ValorTotal = domain.Dinheiro(vt)
		prod.ID = it.ProdutoID
		prod.ValorBase = domain.Dinheiro(vb)
		prod.Ativo = (prodAtivo == 1)
		it.Produto = &prod
		loc.Itens = append(loc.Itens, it)
	}

	return &loc, nil
}

// Listar retorna as locações com filtros opcionais de situação e busca.
func (r *LocacaoRepository) Listar(ctx context.Context, situacao *domain.SituacaoLocacao, busca string) ([]domain.Locacao, error) {
	var sb strings.Builder
	var args []interface{}

	sb.WriteString(`
		SELECT 
			l.id, l.cliente_id, l.tipo_evento_id, l.data_contratacao, l.data_evento,
			l.data_entr_prev, l.data_dev_prev, l.data_entr_real, l.data_dev_real,
			l.valor_total, l.valor_sinal, l.valor_adiantado, l.valor_saldo,
			l.situacao, l.observacoes, l.criado_em,
			c.id, c.nome, c.cpf, c.telefone1
		FROM locacoes l
		JOIN clientes c ON l.cliente_id = c.id
		WHERE 1=1
	`)

	if situacao != nil {
		sb.WriteString(" AND l.situacao = ?")
		args = append(args, int(*situacao))
	}

	busca = strings.TrimSpace(busca)
	if busca != "" {
		sb.WriteString(" AND (c.nome LIKE ? OR c.cpf LIKE ? OR CAST(l.id AS TEXT) = ?)")
		wild := "%" + busca + "%"
		args = append(args, wild, wild, busca)
	}

	sb.WriteString(" ORDER BY l.id DESC LIMIT 200")

	rows, err := r.db.QueryContext(ctx, sb.String(), args...)
	if err != nil {
		return nil, fmt.Errorf("falha ao listar locacoes: %w", err)
	}
	defer rows.Close()

	var lista []domain.Locacao
	for rows.Next() {
		var loc domain.Locacao
		var cli domain.Cliente
		var teID sql.NullInt64
		var dataContrStr, dataEvStr, dataEntrPrevStr, dataDevPrevStr string
		var dataEntrReal, dataDevReal sql.NullTime
		var total, sinal, adiantado, saldo int64
		var sit int
		var observacoes sql.NullString

		if err := rows.Scan(
			&loc.ID, &loc.ClienteID, &teID, &dataContrStr, &dataEvStr,
			&dataEntrPrevStr, &dataDevPrevStr, &dataEntrReal, &dataDevReal,
			&total, &sinal, &adiantado, &saldo,
			&sit, &observacoes, &loc.CriadoEm,
			&cli.ID, &cli.Nome, &cli.CPF, &cli.Telefone1,
		); err != nil {
			return nil, err
		}

		loc.DataContratacao, _ = time.Parse("2006-01-02", dataContrStr)
		loc.DataEvento, _ = time.Parse("2006-01-02", dataEvStr)
		loc.DataEntrPrev, _ = time.Parse("2006-01-02", dataEntrPrevStr)
		loc.DataDevPrev, _ = time.Parse("2006-01-02", dataDevPrevStr)
		if dataEntrReal.Valid {
			loc.DataEntrReal = &dataEntrReal.Time
		}
		if dataDevReal.Valid {
			loc.DataDevReal = &dataDevReal.Time
		}

		loc.ValorTotal = domain.Dinheiro(total)
		loc.ValorSinal = domain.Dinheiro(sinal)
		loc.ValorAdiantado = domain.Dinheiro(adiantado)
		loc.ValorSaldo = domain.Dinheiro(saldo)
		loc.Situacao = domain.SituacaoLocacao(sit)
		loc.Observacoes = observacoes.String
		loc.Cliente = &cli
		if teID.Valid {
			idVal := teID.Int64
			loc.TipoEventoID = &idVal
		}

		lista = append(lista, loc)
	}

	return lista, nil
}

// Excluir remove uma locação (e em cascata seus itens).
func (r *LocacaoRepository) Excluir(ctx context.Context, id int64) error {
	_, err := r.db.ExecContext(ctx, "DELETE FROM locacoes WHERE id = ?", id)
	return err
}

// BuscarLocacoesConflitantes consulta o banco para detectar sobreposição na janela de 7 dias [evento-3, evento+4].
func (r *LocacaoRepository) BuscarLocacoesConflitantes(ctx context.Context, produtoID int64, inicio, fim time.Time, locacaoIgnoradaID int64) ([]domain.Locacao, error) {
	query := `
		SELECT l.id, l.cliente_id, l.data_evento, l.situacao
		FROM locacoes l
		JOIN locacao_produtos lp ON l.id = lp.locacao_id
		WHERE lp.produto_id = ?
		  AND l.data_evento BETWEEN ? AND ?
		  AND l.situacao != ?
		  AND l.id != ?
	`

	rows, err := r.db.QueryContext(ctx, query,
		produtoID,
		inicio.Format("2006-01-02"),
		fim.Format("2006-01-02"),
		int(domain.SituacaoCancelado),
		locacaoIgnoradaID,
	)
	if err != nil {
		return nil, fmt.Errorf("falha ao verificar conflitos de agenda no sqlite: %w", err)
	}
	defer rows.Close()

	var conflitos []domain.Locacao
	for rows.Next() {
		var loc domain.Locacao
		var dataEvStr string
		var sit int
		if err := rows.Scan(&loc.ID, &loc.ClienteID, &dataEvStr, &sit); err != nil {
			return nil, err
		}
		loc.DataEvento, _ = time.Parse("2006-01-02", dataEvStr)
		loc.Situacao = domain.SituacaoLocacao(sit)
		conflitos = append(conflitos, loc)
	}

	return conflitos, nil
}
