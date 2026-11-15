package locacao

import (
	"context"
	"fmt"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/agenda"
	"tech.jefersonms/ducarmolocacoes/internal/core/contrato"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

// Repository define a interface de persistência de locações.
type Repository interface {
	Criar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error)
	Atualizar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error)
	ObterPorID(ctx context.Context, id int64) (*domain.Locacao, error)
	Listar(ctx context.Context, situacao *domain.SituacaoLocacao, busca string) ([]domain.Locacao, error)
	Excluir(ctx context.Context, id int64) error
}

type Service struct {
	repo    Repository
	checker *agenda.Checker
}

func NewService(repo Repository, checker *agenda.Checker) *Service {
	return &Service{
		repo:    repo,
		checker: checker,
	}
}

// Salvar cria ou atualiza uma locação, validando regras de negócio e conflitos de agenda.
func (s *Service) Salvar(ctx context.Context, loc *domain.Locacao) (*domain.Locacao, error) {
	if loc.ClienteID <= 0 {
		return nil, domain.ErrClienteInvalido
	}
	if len(loc.Itens) == 0 {
		return nil, domain.ErrLocacaoSemItens
	}
	if loc.DataEvento.IsZero() || loc.DataEntrPrev.IsZero() || loc.DataDevPrev.IsZero() {
		return nil, domain.ErrDatasInvalidas
	}

	// 1. Recalcula valores totais e saldo
	loc.RecalcularTotais()

	// 2. Valida o sinal mínimo de 20%
	if err := loc.ValidarSinalMinimo(); err != nil {
		return nil, err
	}

	// 3. Valida colisão de agenda com a janela de 7 dias [evento-3, evento+4]
	if err := s.checker.ValidarDisponibilidade(ctx, loc); err != nil {
		return nil, err
	}

	if loc.DataContratacao.IsZero() {
		loc.DataContratacao = time.Now()
	}

	if loc.ID == 0 {
		loc.Situacao = domain.SituacaoNovo
		return s.repo.Criar(ctx, loc)
	}

	return s.repo.Atualizar(ctx, loc)
}

// EmitirContrato transita a locação para a situação 1 e pré-processa o documento.
func (s *Service) EmitirContrato(ctx context.Context, id int64) (*domain.Locacao, error) {
	loc, err := s.repo.ObterPorID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("locação não encontrada: %w", err)
	}

	if err := loc.PodeTransitarPara(domain.SituacaoContratoEmitido); err != nil {
		return nil, err
	}

	loc.Situacao = domain.SituacaoContratoEmitido
	loc.HTMLContrato = contrato.ProcessarTags("Minuta Locação TAG_LOCACAO_NUMERO", loc)

	return s.repo.Atualizar(ctx, loc)
}

// EntregarTrajes transita a locação para a situação 2 exigindo saldo quitado.
func (s *Service) EntregarTrajes(ctx context.Context, id int64) (*domain.Locacao, error) {
	loc, err := s.repo.ObterPorID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("locação não encontrada: %w", err)
	}

	if err := loc.PodeTransitarPara(domain.SituacaoTrajesEntregues); err != nil {
		return nil, err
	}

	agora := time.Now()
	loc.Situacao = domain.SituacaoTrajesEntregues
	loc.DataEntrReal = &agora

	return s.repo.Atualizar(ctx, loc)
}

// DevolverTrajes transita a locação para a situação 3 finalizando o ciclo.
func (s *Service) DevolverTrajes(ctx context.Context, id int64) (*domain.Locacao, error) {
	loc, err := s.repo.ObterPorID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("locação não encontrada: %w", err)
	}

	if err := loc.PodeTransitarPara(domain.SituacaoTrajesDevolvidos); err != nil {
		return nil, err
	}

	agora := time.Now()
	loc.Situacao = domain.SituacaoTrajesDevolvidos
	loc.DataDevReal = &agora

	return s.repo.Atualizar(ctx, loc)
}

// Cancelar cancela a locação e libera os trajes da agenda.
func (s *Service) Cancelar(ctx context.Context, id int64) (*domain.Locacao, error) {
	loc, err := s.repo.ObterPorID(ctx, id)
	if err != nil {
		return nil, fmt.Errorf("locação não encontrada: %w", err)
	}

	if err := loc.PodeTransitarPara(domain.SituacaoCancelado); err != nil {
		return nil, err
	}

	loc.Situacao = domain.SituacaoCancelado
	return s.repo.Atualizar(ctx, loc)
}

func (s *Service) ObterPorID(ctx context.Context, id int64) (*domain.Locacao, error) {
	return s.repo.ObterPorID(ctx, id)
}

func (s *Service) Listar(ctx context.Context, situacao *domain.SituacaoLocacao, busca string) ([]domain.Locacao, error) {
	return s.repo.Listar(ctx, situacao, busca)
}
