package contrato

import (
	"testing"
	"time"

	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

func TestProcessarTags(t *testing.T) {
	modelo := "Contrato: TAG_LOCACAO_NUMERO - Cliente: TAG_CLIENTE_NOME - Total: TAG_LOCACAO_VALOR_TOTAL"
	loc := &domain.Locacao{
		ID:         123,
		ValorTotal: domain.DeReais(250.0),
		Cliente: &domain.Cliente{
			Nome: "Maria da Silva",
		},
	}

	res := ProcessarTags(modelo, loc)
	esperado := "Contrato: 000123 - Cliente: Maria da Silva - Total: R$ 250.00"
	if res != esperado {
		t.Fatalf("esperado '%s', obtido '%s'", esperado, res)
	}
}

func TestGerarPDF(t *testing.T) {
	loc := &domain.Locacao{
		ID:              1,
		DataContratacao: time.Now(),
		DataEvento:      time.Now().AddDate(0, 0, 10),
		DataEntrPrev:    time.Now().AddDate(0, 0, 9),
		DataDevPrev:     time.Now().AddDate(0, 0, 12),
		ValorTotal:      domain.DeReais(300.0),
		ValorSinal:      domain.DeReais(60.0),
		ValorAdiantado:  0,
		ValorSaldo:      domain.DeReais(240.0),
		Cliente: &domain.Cliente{
			Nome:      "João de Souza",
			CPF:       "123.456.789-00",
			RG:        "9876543-2",
			Telefone1: "(41) 99999-8888",
			Endereco:  "Rua das Flores, 100",
			Bairro:    "Centro",
			Cidade:    "Bocaiúva do Sul",
		},
		TipoEvento: &domain.TipoEvento{
			Descricao: "Casamento",
		},
		Itens: []domain.ItemLocacao{
			{
				Quantidade: 1,
				ValorUnit:  domain.DeReais(200.0),
				ValorTotal: domain.DeReais(200.0),
				Produto: &domain.Produto{
					Descricao: "Vestido Noiva Rendado",
				},
			},
			{
				Quantidade: 1,
				ValorUnit:  domain.DeReais(100.0),
				ValorTotal: domain.DeReais(100.0),
				Produto: &domain.Produto{
					Descricao: "Terno Completo Preto",
				},
			},
		},
	}

	pdfBytes, err := GerarPDF(loc)
	if err != nil {
		t.Fatalf("falha ao gerar PDF: %v", err)
	}

	if len(pdfBytes) < 1000 {
		t.Fatalf("PDF gerado é muito pequeno (%d bytes)", len(pdfBytes))
	}

	// Verificar se começa com %PDF
	if string(pdfBytes[:4]) != "%PDF" {
		t.Fatalf("cabeçalho de PDF inválido: %s", string(pdfBytes[:4]))
	}
}
