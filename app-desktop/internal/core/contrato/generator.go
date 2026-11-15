package contrato

import (
	"bytes"
	"fmt"
	"strings"
	"time"

	"github.com/jung-kurt/gofpdf"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
)

// ProcessarTags substitui todas as tags dinâmicas do contrato padrão pelos dados da locação.
func ProcessarTags(modelo string, loc *domain.Locacao) string {
	cli := loc.Cliente
	if cli == nil {
		cli = &domain.Cliente{}
	}

	tipoEvento := "Não informado"
	if loc.TipoEvento != nil && loc.TipoEvento.Descricao != "" {
		tipoEvento = loc.TipoEvento.Descricao
	}

	var sbItens strings.Builder
	for i, it := range loc.Itens {
		nome := "Item"
		if it.Produto != nil && it.Produto.Descricao != "" {
			nome = it.Produto.Descricao
		}
		sbItens.WriteString(fmt.Sprintf("%d) %s - Qtd: %d - Unit: %s - Total: %s\n",
			i+1, nome, it.Quantidade, it.ValorUnit.FormatBR(), it.ValorTotal.FormatBR()))
	}

	enderecoCompleto := cli.Endereco
	if cli.Bairro != "" {
		enderecoCompleto += " - " + cli.Bairro
	}
	if cli.Cidade != "" {
		enderecoCompleto += " - " + cli.Cidade
	}
	if cli.CEP != "" {
		enderecoCompleto += " (CEP: " + cli.CEP + ")"
	}

	replacer := strings.NewReplacer(
		"TAG_LOCACAO_NUMERO", fmt.Sprintf("%06d", loc.ID),
		"TAG_CLIENTE_NOME", cli.Nome,
		"TAG_TIPO_EVENTO", tipoEvento,
		"TAG_CLIENTE_CPF", cli.CPF,
		"TAG_CLIENTE_RG", cli.RG,
		"TAG_CLIENTE_NASCIMENTO", "-",
		"TAG_CLIENTE_ENDERECO", enderecoCompleto,
		"TAG_CLIENTE_FONE_RESIDENCIAL", cli.Telefone1,
		"TAG_CLIENTE_FONE_COMERCIAL", cli.Telefone2,
		"TAG_CLIENTE_FONE_RECADO", "-",
		"TAG_LOCACAO_VALOR_TOTAL", loc.ValorTotal.FormatBR(),
		"TAG_LOCACAO_VALOR_SINAL", loc.ValorSinal.FormatBR(),
		"TAG_LOCACAO_VALOR_ADIANTADO", loc.ValorAdiantado.FormatBR(),
		"TAG_LOCACAO_VALOR_SALDO", loc.ValorSaldo.FormatBR(),
		"TAG_LOCACAO_DATA_ENTREGA_PREVISTA", loc.DataEntrPrev.Format("02/01/2006"),
		"TAG_LOCACAO_DATA_DEVOLUCAO_PREVISTA", loc.DataDevPrev.Format("02/01/2006"),
		"TAG_LOCACAO_DATA_CONTRATACAO", loc.DataContratacao.Format("02/01/2006"),
		"TAG_LOCACAO_DATA_EVENTO", loc.DataEvento.Format("02/01/2006"),
		"TAG_LOCACAO_ITEM_EM_TABELA", sbItens.String(),
		"TAG_LOCACAO_ITEM_EM_LINHA", sbItens.String(),
	)

	return replacer.Replace(modelo)
}

// GerarPDF compila a minuta e os dados da locação em um arquivo PDF pronto para impressão física.
func GerarPDF(loc *domain.Locacao) ([]byte, error) {
	pdf := gofpdf.New("P", "mm", "A4", "")
	pdf.SetMargins(15, 12, 15)
	pdf.AddPage()

	// Cabeçalho Principal
	pdf.SetFont("Arial", "B", 15)
	pdf.SetTextColor(0, 51, 153)
	pdf.CellFormat(0, 7, "DU CARMO LOCAÇÕES", "", 1, "C", false, 0, "")

	pdf.SetFont("Arial", "B", 12)
	pdf.SetTextColor(40, 40, 40)
	pdf.CellFormat(0, 6, "CONTRATO DE LOCAÇÃO DE TRAJES E ACESSÓRIOS", "", 1, "C", false, 0, "")
	pdf.SetFont("Arial", "I", 9)
	pdf.CellFormat(0, 5, "Locadora: Maria do Carmo Poli - Rua Benjamin Constant Teixeira, 929 - Centro - Bocaiúva do Sul - PR", "", 1, "C", false, 0, "")
	pdf.CellFormat(0, 4, "Fones: (41) 8415-4842 / (41) 99933-2404", "", 1, "C", false, 0, "")
	pdf.Ln(4)

	// Linha divisória
	pdf.SetDrawColor(180, 180, 180)
	pdf.Line(15, pdf.GetY(), 195, pdf.GetY())
	pdf.Ln(3)

	// Dados do Cliente e Evento
	cli := loc.Cliente
	if cli == nil {
		cli = &domain.Cliente{}
	}

	tipoEvento := "Não informado"
	if loc.TipoEvento != nil && loc.TipoEvento.Descricao != "" {
		tipoEvento = loc.TipoEvento.Descricao
	}

	pdf.SetFont("Arial", "B", 10)
	pdf.SetFillColor(240, 243, 246)
	pdf.CellFormat(0, 6, fmt.Sprintf(" LOCAÇÃO Nº %06d | EVENTO: %s", loc.ID, tipoEvento), "1", 1, "L", true, 0, "")

	pdf.SetFont("Arial", "", 9)
	pdf.CellFormat(120, 6, fmt.Sprintf("Locatário: %s", cli.Nome), "LR", 0, "L", false, 0, "")
	pdf.CellFormat(60, 6, fmt.Sprintf("CPF: %s", cli.CPF), "R", 1, "L", false, 0, "")

	pdf.CellFormat(120, 6, fmt.Sprintf("Endereço: %s, %s - %s", cli.Endereco, cli.Bairro, cli.Cidade), "LR", 0, "L", false, 0, "")
	pdf.CellFormat(60, 6, fmt.Sprintf("RG: %s", cli.RG), "R", 1, "L", false, 0, "")

	pdf.CellFormat(90, 6, fmt.Sprintf("Telefone Principal: %s", cli.Telefone1), "LBR", 0, "L", false, 0, "")
	pdf.CellFormat(90, 6, fmt.Sprintf("Telefone Secundário: %s", cli.Telefone2), "BR", 1, "L", false, 0, "")
	pdf.Ln(2)

	// Quadro Financeiro e Datas
	pdf.SetFont("Arial", "B", 9)
	pdf.SetFillColor(230, 238, 248)
	pdf.CellFormat(45, 6, "VALOR TOTAL", "1", 0, "C", true, 0, "")
	pdf.CellFormat(45, 6, "SINAL (MÍN. 20%)", "1", 0, "C", true, 0, "")
	pdf.CellFormat(45, 6, "ADIANTADO", "1", 0, "C", true, 0, "")
	pdf.CellFormat(45, 6, "SALDO RETIRADA", "1", 1, "C", true, 0, "")

	pdf.SetFont("Arial", "B", 10)
	pdf.CellFormat(45, 7, loc.ValorTotal.FormatBR(), "1", 0, "C", false, 0, "")
	pdf.CellFormat(45, 7, loc.ValorSinal.FormatBR(), "1", 0, "C", false, 0, "")
	pdf.CellFormat(45, 7, loc.ValorAdiantado.FormatBR(), "1", 0, "C", false, 0, "")
	pdf.SetTextColor(180, 0, 0)
	pdf.CellFormat(45, 7, loc.ValorSaldo.FormatBR(), "1", 1, "C", false, 0, "")
	pdf.SetTextColor(0, 0, 0)

	pdf.SetFont("Arial", "", 8.5)
	pdf.CellFormat(60, 5, fmt.Sprintf("Data Evento: %s", loc.DataEvento.Format("02/01/2006")), "1", 0, "C", false, 0, "")
	pdf.CellFormat(60, 5, fmt.Sprintf("Retirada: %s (após 16h)", loc.DataEntrPrev.Format("02/01/2006")), "1", 0, "C", false, 0, "")
	pdf.CellFormat(60, 5, fmt.Sprintf("Devolução: %s", loc.DataDevPrev.Format("02/01/2006")), "1", 1, "C", false, 0, "")
	pdf.Ln(2)

	// Itens da Locação
	pdf.SetFont("Arial", "B", 9)
	pdf.SetFillColor(240, 240, 240)
	pdf.CellFormat(0, 5, "TRAJES E ACESSÓRIOS DISCRIMINADOS", "1", 1, "L", true, 0, "")
	pdf.SetFont("Arial", "", 8.5)
	for i, item := range loc.Itens {
		desc := "Traje"
		if item.Produto != nil {
			desc = item.Produto.Descricao
		}
		pdf.CellFormat(10, 5, fmt.Sprintf("%d", i+1), "1", 0, "C", false, 0, "")
		pdf.CellFormat(120, 5, desc, "1", 0, "L", false, 0, "")
		pdf.CellFormat(20, 5, fmt.Sprintf("Qtd: %d", item.Quantidade), "1", 0, "C", false, 0, "")
		pdf.CellFormat(30, 5, item.ValorTotal.FormatBR(), "1", 1, "R", false, 0, "")
	}
	pdf.Ln(2)

	// Cláusulas Contratuais Essenciais
	pdf.SetFont("Arial", "B", 8)
	pdf.CellFormat(0, 4, "CLÁUSULAS CONTRATUAIS:", "", 1, "L", false, 0, "")
	pdf.SetFont("Arial", "", 7.5)

	clausulas := []string{
		"1. O Locatário deverá pagar sinal mínimo de 20% para confirmação da reserva. Em caso de desistência em prazo inferior a 10 dias do evento, o sinal será retido pela locadora.",
		"2. O SALDO DEVEDOR DEVERÁ SER QUITADO INTEGRALMENTE ATÉ A RETIRADA DO TRAJE. A locadora fica autorizada a reter os trajes caso o pagamento não seja concluído.",
		"3. Os trajes e acessórios serão entregues em perfeitas condições. Danos, rasgos, manchas ou perda ensejarão indenização calculada em até 4 vezes o valor da locação.",
		"4. É expressamente proibido lavar, passar, bordar, ajustar ou costurar os trajes sem consentimento prévio e expresso da locadora.",
		"5. O atraso na devolução implicará em multa diária de 20% sobre o valor total do contrato por dia de atraso.",
		"6. A locadora não se responsabiliza por trajes não provados com antecedência ou cedidos a terceiros.",
	}

	for _, cl := range clausulas {
		pdf.MultiCell(0, 3.8, cl, "", "J", false)
	}

	pdf.Ln(5)

	// Assinaturas
	pdf.SetFont("Arial", "", 8.5)
	dataStr := time.Now().Format("02 de Janeiro de 2006")
	cidadeData := fmt.Sprintf("Bocaiúva do Sul - PR, %s", dataStr)
	pdf.CellFormat(0, 4, cidadeData, "", 1, "C", false, 0, "")
	pdf.Ln(10)

	pdf.Line(25, pdf.GetY(), 90, pdf.GetY())
	pdf.Line(120, pdf.GetY(), 185, pdf.GetY())
	pdf.CellFormat(95, 4, "LOCADORA (Du Carmo Locações)", "", 0, "C", false, 0, "")
	pdf.CellFormat(95, 4, "LOCATÁRIO", "", 1, "C", false, 0, "")

	var buf bytes.Buffer
	err := pdf.Output(&buf)
	if err != nil {
		return nil, fmt.Errorf("falha ao renderizar PDF: %w", err)
	}

	return buf.Bytes(), nil
}
