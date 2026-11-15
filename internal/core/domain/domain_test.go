package domain

import (
	"testing"
)

func TestRecalcularTotais(t *testing.T) {
	loc := Locacao{
		ValorSinal:     DeReais(50.0),
		ValorAdiantado: DeReais(30.0),
		Itens: []ItemLocacao{
			{Quantidade: 1, ValorUnit: DeReais(150.0)},
			{Quantidade: 2, ValorUnit: DeReais(25.0)},
		},
	}

	loc.RecalcularTotais()

	if loc.ValorTotal != DeReais(200.0) {
		t.Fatalf("esperado total R$ 200.00, obtido: %s", loc.ValorTotal.FormatBR())
	}

	// 200 - 50 - 30 = 120
	if loc.ValorSaldo != DeReais(120.0) {
		t.Fatalf("esperado saldo R$ 120.00, obtido: %s", loc.ValorSaldo.FormatBR())
	}
}

func TestValidarSinalMinimo(t *testing.T) {
	loc := Locacao{
		ValorTotal: DeReais(200.0), // 20% é R$ 40,00
		ValorSinal: DeReais(39.0),
	}

	if err := loc.ValidarSinalMinimo(); err != ErrSinalInsuficiente {
		t.Fatalf("esperado ErrSinalInsuficiente com sinal menor que 20%%, obtido: %v", err)
	}

	loc.ValorSinal = DeReais(40.0)
	if err := loc.ValidarSinalMinimo(); err != nil {
		t.Fatalf("esperado sucesso com sinal igual a 20%%, obtido: %v", err)
	}

	loc.ValorSinal = DeReais(50.0)
	if err := loc.ValidarSinalMinimo(); err != nil {
		t.Fatalf("esperado sucesso com sinal maior que 20%%, obtido: %v", err)
	}
}

func TestMaquinaDeEstados(t *testing.T) {
	loc := Locacao{
		Situacao:   SituacaoNovo,
		ValorTotal: DeReais(100.0),
		ValorSinal: DeReais(20.0),
		ValorSaldo: DeReais(80.0),
	}

	// 0 -> 1 permitido
	if err := loc.PodeTransitarPara(SituacaoContratoEmitido); err != nil {
		t.Fatalf("transicao 0 -> 1 falhou: %v", err)
	}

	// 0 -> 2 não permitido diretamente
	if err := loc.PodeTransitarPara(SituacaoTrajesEntregues); err == nil {
		t.Fatalf("transicao 0 -> 2 deveria ser proibida")
	}

	// 1 -> 2 bloqueado enquanto saldo > 0
	loc.Situacao = SituacaoContratoEmitido
	if err := loc.PodeTransitarPara(SituacaoTrajesEntregues); err == nil {
		t.Fatalf("entrega de trajes com saldo pendente deveria falhar")
	}

	// Quita saldo e tenta novamente
	loc.ValorSaldo = 0
	if err := loc.PodeTransitarPara(SituacaoTrajesEntregues); err != nil {
		t.Fatalf("entrega de trajes com saldo quitado falhou: %v", err)
	}

	// 2 -> 3 permitido
	loc.Situacao = SituacaoTrajesEntregues
	if err := loc.PodeTransitarPara(SituacaoTrajesDevolvidos); err != nil {
		t.Fatalf("transicao 2 -> 3 falhou: %v", err)
	}
}
