package domain

import (
	"errors"
	"fmt"
	"time"
)

var (
	ErrSinalInsuficiente    = errors.New("o valor do sinal deve ser de no mínimo 20% do valor total")
	ErrConflitoAgenda       = errors.New("conflito de agenda: o traje já está reservado no período de 7 dias [evento-3, evento+4]")
	ErrSaldoPendente        = errors.New("a entrega dos trajes exige a quitação integral do saldo devedor")
	ErrTransicaoInvalida    = errors.New("transição de situação inválida para a locação")
	ErrClienteInvalido      = errors.New("dados do cliente inválidos ou incompletos")
	ErrLocacaoSemItens      = errors.New("a locação deve conter ao menos um traje")
	ErrDatasInvalidas       = errors.New("as datas de entrega e devolução devem ser coerentes com a data do evento")
)

type SituacaoLocacao int

const (
	SituacaoNovo             SituacaoLocacao = 0
	SituacaoContratoEmitido  SituacaoLocacao = 1
	SituacaoTrajesEntregues  SituacaoLocacao = 2
	SituacaoTrajesDevolvidos SituacaoLocacao = 3
	SituacaoCancelado        SituacaoLocacao = 9
)

func (s SituacaoLocacao) String() string {
	switch s {
	case SituacaoNovo:
		return "Novo"
	case SituacaoContratoEmitido:
		return "Contrato Emitido"
	case SituacaoTrajesEntregues:
		return "Trajes Entregues"
	case SituacaoTrajesDevolvidos:
		return "Trajes Devolvidos"
	case SituacaoCancelado:
		return "Cancelado"
	default:
		return "Desconhecido"
	}
}

// Dinheiro representa quantias monetárias em centavos inteiros (int64) para evitar perdas de precisão flutuante.
type Dinheiro int64

func (d Dinheiro) Float64() float64 {
	return float64(d) / 100.0
}

func (d Dinheiro) FormatBR() string {
	return fmt.Sprintf("R$ %.2f", d.Float64())
}

func DeReais(valor float64) Dinheiro {
	return Dinheiro(int64(valor*100.0 + 0.5))
}

type Cliente struct {
	ID        int64     `json:"id"`
	Nome      string    `json:"nome"`
	CPF       string    `json:"cpf"`
	RG        string    `json:"rg"`
	Telefone1 string    `json:"telefone1"`
	Telefone2 string    `json:"telefone2"`
	Endereco  string    `json:"endereco"`
	Bairro    string    `json:"bairro"`
	Cidade    string    `json:"cidade"`
	CEP       string    `json:"cep"`
	CriadoEm  time.Time `json:"criado_em"`
}

type Produto struct {
	ID        int64    `json:"id"`
	Descricao string   `json:"descricao"`
	ValorBase Dinheiro `json:"valor_base"`
	Ativo     bool     `json:"ativo"`
}

type TipoEvento struct {
	ID        int64  `json:"id"`
	Descricao string `json:"descricao"`
}

type ItemLocacao struct {
	ID         int64    `json:"id"`
	LocacaoID  int64    `json:"locacao_id"`
	ProdutoID  int64    `json:"produto_id"`
	Produto    *Produto `json:"produto,omitempty"`
	Quantidade int      `json:"quantidade"`
	ValorUnit  Dinheiro `json:"valor_unit"`
	ValorTotal Dinheiro `json:"valor_total"`
}

type Locacao struct {
	ID              int64           `json:"id"`
	ClienteID       int64           `json:"cliente_id"`
	Cliente         *Cliente        `json:"cliente,omitempty"`
	TipoEventoID    *int64          `json:"tipo_evento_id,omitempty"`
	TipoEvento      *TipoEvento     `json:"tipo_evento,omitempty"`
	DataContratacao time.Time       `json:"data_contratacao"`
	DataEvento      time.Time       `json:"data_evento"`
	DataEntrPrev    time.Time       `json:"data_entr_prev"`
	DataDevPrev     time.Time       `json:"data_dev_prev"`
	DataEntrReal    *time.Time      `json:"data_entr_real,omitempty"`
	DataDevReal     *time.Time      `json:"data_dev_real,omitempty"`
	ValorTotal      Dinheiro        `json:"valor_total"`
	ValorSinal      Dinheiro        `json:"valor_sinal"`
	ValorAdiantado  Dinheiro        `json:"valor_adiantado"`
	ValorSaldo      Dinheiro        `json:"valor_saldo"`
	Situacao        SituacaoLocacao `json:"situacao"`
	HTMLContrato    string          `json:"html_contrato,omitempty"`
	Observacoes     string          `json:"observacoes,omitempty"`
	Itens           []ItemLocacao   `json:"itens"`
	CriadoEm        time.Time       `json:"criado_em"`
}

// RecalcularTotais calcula a soma dos itens e força o saldo devedor.
func (l *Locacao) RecalcularTotais() {
	var total Dinheiro
	for i := range l.Itens {
		l.Itens[i].ValorTotal = l.Itens[i].ValorUnit * Dinheiro(l.Itens[i].Quantidade)
		total += l.Itens[i].ValorTotal
	}
	l.ValorTotal = total
	l.ValorSaldo = l.ValorTotal - l.ValorSinal - l.ValorAdiantado
}

// ValidarSinalMinimo verifica a regra de negócio do sinal mínimo de 20%.
func (l *Locacao) ValidarSinalMinimo() error {
	if l.ValorTotal <= 0 {
		return nil
	}
	sinalMinimo := Dinheiro(int64(float64(l.ValorTotal) * 0.20))
	if l.ValorSinal < sinalMinimo {
		return ErrSinalInsuficiente
	}
	return nil
}

// ValidarQuitacaoParaEntrega assegura que os trajes só saem se o saldo for zero.
func (l *Locacao) ValidarQuitacaoParaEntrega() error {
	if l.ValorSaldo > 0 {
		return fmt.Errorf("%w: saldo devedor de %s pendente", ErrSaldoPendente, l.ValorSaldo.FormatBR())
	}
	return nil
}

// PodeTransitarPara valida a máquina de estados oficial do sistema.
func (l *Locacao) PodeTransitarPara(novoStatus SituacaoLocacao) error {
	if l.Situacao == novoStatus {
		return nil
	}

	switch l.Situacao {
	case SituacaoNovo:
		if novoStatus == SituacaoContratoEmitido || novoStatus == SituacaoCancelado {
			return nil
		}
	case SituacaoContratoEmitido:
		if novoStatus == SituacaoTrajesEntregues {
			return l.ValidarQuitacaoParaEntrega()
		}
		if novoStatus == SituacaoCancelado {
			return nil
		}
	case SituacaoTrajesEntregues:
		if novoStatus == SituacaoTrajesDevolvidos {
			return nil
		}
	case SituacaoTrajesDevolvidos:
		return errors.New("locação já finalizada com devolução concluída")
	case SituacaoCancelado:
		return errors.New("locação cancelada não pode ser reativada")
	}

	return fmt.Errorf("%w: de %s para %s", ErrTransicaoInvalida, l.Situacao, novoStatus)
}
