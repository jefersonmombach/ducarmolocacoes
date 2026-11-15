package page

import (
	"context"
	"fmt"
	"time"

	"gioui.org/layout"
	"gioui.org/unit"
	gwidget "gioui.org/widget"
	"gioui.org/widget/material"
	"tech.jefersonms/ducarmolocacoes/internal/core/contrato"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/core/locacao"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
	"tech.jefersonms/ducarmolocacoes/internal/ui/widget"
)

type LocacaoFormPage struct {
	svc          *locacao.Service
	cliRepo      *sqlite.ClienteRepository
	prodRepo     *sqlite.ProdutoRepository
	modal        *widget.Modal
	onSalvo      func()
	onCancelar   func()

	// Listas de apoio
	clientes     []domain.Cliente
	produtos     []domain.Produto
	cliBtns      []gwidget.Clickable
	prodBtns     []gwidget.Clickable
	removeBtns   []gwidget.Clickable

	// Estado do formulário
	clienteSelecionado *domain.Cliente
	itens              []domain.ItemLocacao
	dataEventoEd       gwidget.Editor
	dataRetiradaEd     gwidget.Editor
	dataDevolucaoEd    gwidget.Editor
	sinalEd            gwidget.Editor
	adiantadoEd        gwidget.Editor
	obsEd              gwidget.Editor

	btnSalvar          gwidget.Clickable
	btnSalvarEPDF      gwidget.Clickable
	btnCancelar        gwidget.Clickable
}

func NewLocacaoFormPage(
	svc *locacao.Service,
	cliRepo *sqlite.ClienteRepository,
	prodRepo *sqlite.ProdutoRepository,
	modal *widget.Modal,
	onSalvo func(),
	onCancelar func(),
) *LocacaoFormPage {
	p := &LocacaoFormPage{
		svc:        svc,
		cliRepo:    cliRepo,
		prodRepo:   prodRepo,
		modal:      modal,
		onSalvo:    onSalvo,
		onCancelar: onCancelar,
	}

	p.Inicializar()
	return p
}

func (p *LocacaoFormPage) Inicializar() {
	ctx := context.Background()
	p.clientes, _ = p.cliRepo.Listar(ctx, "")
	p.produtos, _ = p.prodRepo.Listar(ctx, true, "")
	p.cliBtns = make([]gwidget.Clickable, len(p.clientes))
	p.prodBtns = make([]gwidget.Clickable, len(p.produtos))

	if len(p.clientes) > 0 {
		p.clienteSelecionado = &p.clientes[0]
	}

	hoje := time.Now()
	ev := hoje.AddDate(0, 0, 7)
	p.dataEventoEd.SetText(ev.Format("02/01/2006"))
	p.dataRetiradaEd.SetText(ev.AddDate(0, 0, -1).Format("02/01/2006"))
	p.dataDevolucaoEd.SetText(ev.AddDate(0, 0, 2).Format("02/01/2006"))
	p.sinalEd.SetText("0.00")
	p.adiantadoEd.SetText("0.00")
	p.itens = nil
	p.removeBtns = nil
}

func (p *LocacaoFormPage) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	// Ação Cancelar
	if p.btnCancelar.Clicked(gtx) {
		if p.onCancelar != nil {
			p.onCancelar()
		}
	}

	// Ação Salvar Simples
	if p.btnSalvar.Clicked(gtx) {
		p.salvar(false)
	}

	// Ação Salvar e Gerar PDF
	if p.btnSalvarEPDF.Clicked(gtx) {
		p.salvar(true)
	}

	// Seleção de cliente
	for i := range p.clientes {
		if p.cliBtns[i].Clicked(gtx) {
			p.clienteSelecionado = &p.clientes[i]
		}
	}

	// Adicionar produto
	for i := range p.produtos {
		if p.prodBtns[i].Clicked(gtx) {
			prod := p.produtos[i]
			p.adicionarItem(&prod)
		}
	}

	// Remover item
	for i := range p.removeBtns {
		if i < len(p.itens) && p.removeBtns[i].Clicked(gtx) {
			p.removerItem(i)
			break
		}
	}

	return layout.UniformInset(unit.Dp(20)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
			// Header
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				lbl := material.H5(th.Theme, "Nova Locação de Traje")
				lbl.Color = theme.ColorText
				return lbl.Layout(gtx)
			}),
			layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

			// Grid 2 Colunas: Esquerda (Dados/Datas) e Direita (Itens/Financeiro)
			layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Spacing: layout.SpaceBetween}.Layout(gtx,
					// Coluna Esquerda
					layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
						return p.layoutColunaEsquerda(gtx, th)
					}),
					layout.Rigid(layout.Spacer{Width: unit.Dp(20)}.Layout),
					// Coluna Direita
					layout.Flexed(1.2, func(gtx layout.Context) layout.Dimensions {
						return p.layoutColunaDireita(gtx, th)
					}),
				)
			}),

			layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

			// Botões de Ação no Rodapé
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Spacing: layout.SpaceEnd, Alignment: layout.Middle}.Layout(gtx,
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnCancelar, "Cancelar (ESC)")
						btn.Background = theme.ColorSurfaceAlt
						btn.Color = theme.ColorText
						return btn.Layout(gtx)
					}),
					layout.Rigid(layout.Spacer{Width: unit.Dp(12)}.Layout),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnSalvar, "Gravar Reserva")
						btn.Background = theme.ColorPrimaryDark
						btn.Color = theme.ColorText
						return btn.Layout(gtx)
					}),
					layout.Rigid(layout.Spacer{Width: unit.Dp(12)}.Layout),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnSalvarEPDF, "Gravar e Emitir PDF 📄")
						btn.Background = theme.ColorSuccess
						btn.Color = theme.ColorText
						return btn.Layout(gtx)
					}),
				)
			}),
		)
	})
}

func (p *LocacaoFormPage) layoutColunaEsquerda(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	card := widget.NewCard()
	return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.UniformInset(unit.Dp(16)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
			return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Subtitle1(th.Theme, "1. Locatário / Cliente")
					lbl.Color = theme.ColorPrimary
					return lbl.Layout(gtx)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

				// Cliente selecionado em destaque
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					nome := "Nenhum cliente selecionado"
					cpf := ""
					if p.clienteSelecionado != nil {
						nome = p.clienteSelecionado.Nome
						cpf = "CPF: " + p.clienteSelecionado.CPF + " | " + p.clienteSelecionado.Telefone1
					}
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							lbl := material.Body1(th.Theme, nome)
							lbl.Color = theme.ColorText
							return lbl.Layout(gtx)
						}),
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							lbl := material.Caption(th.Theme, cpf)
							lbl.Color = theme.ColorTextMuted
							return lbl.Layout(gtx)
						}),
					)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(12)}.Layout),

				// Mini lista de seleção rápida de clientes
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Clique para alterar cliente:")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					var cliItems []layout.FlexChild
					maxShow := 3
					if len(p.clientes) < maxShow {
						maxShow = len(p.clientes)
					}
					for i := 0; i < maxShow; i++ {
						idx := i
						cliItems = append(cliItems, layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							btn := material.Button(th.Theme, &p.cliBtns[idx], p.clientes[idx].Nome)
							btn.Background = theme.ColorSurfaceAlt
							btn.TextSize = unit.Sp(12)
							return btn.Layout(gtx)
						}))
						cliItems = append(cliItems, layout.Rigid(layout.Spacer{Width: unit.Dp(6)}.Layout))
					}
					return layout.Flex{}.Layout(gtx, cliItems...)
				}),

				layout.Rigid(layout.Spacer{Height: unit.Dp(20)}.Layout),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Subtitle1(th.Theme, "2. Datas do Contrato")
					lbl.Color = theme.ColorPrimary
					return lbl.Layout(gtx)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Data do Evento (DD/MM/AAAA):")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					ed := material.Editor(th.Theme, &p.dataEventoEd, "ex: 20/12/2026")
					return ed.Layout(gtx)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(10)}.Layout),

				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Previsão de Retirada (após as 16h):")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					ed := material.Editor(th.Theme, &p.dataRetiradaEd, "ex: 19/12/2026")
					return ed.Layout(gtx)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(10)}.Layout),

				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Previsão de Devolução:")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					ed := material.Editor(th.Theme, &p.dataDevolucaoEd, "ex: 22/12/2026")
					return ed.Layout(gtx)
				}),
			)
		})
	})
}

func (p *LocacaoFormPage) layoutColunaDireita(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	card := widget.NewCard()
	return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.UniformInset(unit.Dp(16)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
			return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Subtitle1(th.Theme, "3. Trajes e Vestuário")
					lbl.Color = theme.ColorPrimary
					return lbl.Layout(gtx)
				}),
				layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

				// Catálogo rápido de trajes para adicionar
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Adicionar traje ao contrato:")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					var prodItems []layout.FlexChild
					maxShow := 4
					if len(p.produtos) < maxShow {
						maxShow = len(p.produtos)
					}
					for i := 0; i < maxShow; i++ {
						idx := i
						prodItems = append(prodItems, layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							btn := material.Button(th.Theme, &p.prodBtns[idx], fmt.Sprintf("+ %s (%s)", p.produtos[idx].Descricao, p.produtos[idx].ValorBase.FormatBR()))
							btn.Background = theme.ColorSurfaceAlt
							btn.TextSize = unit.Sp(11)
							return btn.Layout(gtx)
						}))
						prodItems = append(prodItems, layout.Rigid(layout.Spacer{Height: unit.Dp(4)}.Layout))
					}
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx, prodItems...)
				}),

				layout.Rigid(layout.Spacer{Height: unit.Dp(12)}.Layout),

				// Tabela de itens adicionados
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					if len(p.itens) == 0 {
						lbl := material.Caption(th.Theme, "Nenhum traje adicionado ainda. Clique em um traje acima.")
						lbl.Color = theme.ColorWarning
						return lbl.Layout(gtx)
					}

					var rows []layout.FlexChild
					for i := range p.itens {
						idx := i
						it := &p.itens[idx]
						desc := "Traje"
						if it.Produto != nil {
							desc = it.Produto.Descricao
						}
						rows = append(rows, layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									lbl := material.Body2(th.Theme, fmt.Sprintf("%d) %s", idx+1, desc))
									lbl.Color = theme.ColorText
									return lbl.Layout(gtx)
								}),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									lbl := material.Body2(th.Theme, it.ValorTotal.FormatBR())
									lbl.Color = theme.ColorText
									return lbl.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(8)}.Layout),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									btn := material.Button(th.Theme, &p.removeBtns[idx], "✕")
									btn.Background = theme.ColorDanger
									btn.TextSize = unit.Sp(10)
									return btn.Layout(gtx)
								}),
							)
						}))
						rows = append(rows, layout.Rigid(layout.Spacer{Height: unit.Dp(4)}.Layout))
					}
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx, rows...)
				}),

				layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

				// 4. Resumo Financeiro Obrigatório
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					return p.layoutResumoFinanceiro(gtx, th)
				}),
			)
		})
	})
}

func (p *LocacaoFormPage) layoutResumoFinanceiro(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	total := p.calcularTotal()
	sinal := parseDinheiroBR(p.sinalEd.Text())
	adiantado := parseDinheiroBR(p.adiantadoEd.Text())
	saldo := total - sinal - adiantado

	sinalMin := domain.Dinheiro(int64(float64(total) * 0.20))

	return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			lbl := material.Subtitle1(th.Theme, "4. Resumo Financeiro & Regra dos 20%")
			lbl.Color = theme.ColorPrimary
			return lbl.Layout(gtx)
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			lbl := material.H6(th.Theme, fmt.Sprintf("VALOR TOTAL: %s", total.FormatBR()))
			lbl.Color = theme.ColorText
			return lbl.Layout(gtx)
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(6)}.Layout),

		// Campo Sinal (com alerta de mínimo)
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			corAviso := theme.ColorTextMuted
			if sinal < sinalMin && total > 0 {
				corAviso = theme.ColorDanger
			}
			return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, fmt.Sprintf("Valor Sinal (Mínimo 20%%: %s):", sinalMin.FormatBR()))
					lbl.Color = corAviso
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					ed := material.Editor(th.Theme, &p.sinalEd, "0.00")
					return ed.Layout(gtx)
				}),
			)
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(6)}.Layout),

		// Campo Adiantado
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					lbl := material.Caption(th.Theme, "Valor Adiantado:")
					lbl.Color = theme.ColorTextMuted
					return lbl.Layout(gtx)
				}),
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					ed := material.Editor(th.Theme, &p.adiantadoEd, "0.00")
					return ed.Layout(gtx)
				}),
			)
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

		// Saldo Devedor Forçado
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			corSaldo := theme.ColorSuccess
			if saldo > 0 {
				corSaldo = theme.ColorDanger
			}
			lbl := material.Subtitle1(th.Theme, fmt.Sprintf("SALDO NA RETIRADA: %s", saldo.FormatBR()))
			lbl.Color = corSaldo
			return lbl.Layout(gtx)
		}),
	)
}

func (p *LocacaoFormPage) calcularTotal() domain.Dinheiro {
	var total domain.Dinheiro
	for _, it := range p.itens {
		total += it.ValorTotal
	}
	return total
}

func (p *LocacaoFormPage) adicionarItem(prod *domain.Produto) {
	it := domain.ItemLocacao{
		ProdutoID:  prod.ID,
		Produto:    prod,
		Quantidade: 1,
		ValorUnit:  prod.ValorBase,
		ValorTotal: prod.ValorBase,
	}
	p.itens = append(p.itens, it)
	p.removeBtns = make([]gwidget.Clickable, len(p.itens))

	// Sugere sinal de 20%
	total := p.calcularTotal()
	sinalMin := float64(total) * 0.20 / 100.0
	p.sinalEd.SetText(fmt.Sprintf("%.2f", sinalMin))
}

func (p *LocacaoFormPage) removerItem(index int) {
	if index < 0 || index >= len(p.itens) {
		return
	}
	p.itens = append(p.itens[:index], p.itens[index+1:]...)
	p.removeBtns = make([]gwidget.Clickable, len(p.itens))

	total := p.calcularTotal()
	sinalMin := float64(total) * 0.20 / 100.0
	p.sinalEd.SetText(fmt.Sprintf("%.2f", sinalMin))
}

func (p *LocacaoFormPage) salvar(emitirPDF bool) {
	if p.clienteSelecionado == nil {
		p.modal.Show("Erro de Validação", "Por favor, selecione um cliente.", true)
		return
	}
	if len(p.itens) == 0 {
		p.modal.Show("Erro de Validação", "Por favor, adicione ao menos um traje ao contrato.", true)
		return
	}

	dataEv, err := parseDataBR(p.dataEventoEd.Text())
	if err != nil {
		p.modal.Show("Data Inválida", "Formato da data do evento deve ser DD/MM/AAAA.", true)
		return
	}
	dataEntr, err := parseDataBR(p.dataRetiradaEd.Text())
	if err != nil {
		p.modal.Show("Data Inválida", "Formato da data de retirada deve ser DD/MM/AAAA.", true)
		return
	}
	dataDev, err := parseDataBR(p.dataDevolucaoEd.Text())
	if err != nil {
		p.modal.Show("Data Inválida", "Formato da data de devolução deve ser DD/MM/AAAA.", true)
		return
	}

	loc := &domain.Locacao{
		ClienteID:       p.clienteSelecionado.ID,
		Cliente:         p.clienteSelecionado,
		DataContratacao: time.Now(),
		DataEvento:      dataEv,
		DataEntrPrev:    dataEntr,
		DataDevPrev:     dataDev,
		ValorSinal:      parseDinheiroBR(p.sinalEd.Text()),
		ValorAdiantado:  parseDinheiroBR(p.adiantadoEd.Text()),
		Itens:           p.itens,
	}

	ctx := context.Background()
	salva, err := p.svc.Salvar(ctx, loc)
	if err != nil {
		p.modal.Show("Bloqueio de Regra de Negócio", err.Error(), true)
		return
	}

	if emitirPDF {
		emitida, err := p.svc.EmitirContrato(ctx, salva.ID)
		if err == nil {
			salva = emitida
			pdfBytes, _ := contrato.GerarPDF(salva)
			_ = writeFileBytes(fmt.Sprintf("contrato_locacao_%05d.pdf", salva.ID), pdfBytes)
		}
	}

	p.modal.Show("Sucesso", fmt.Sprintf("Locação #%05d cadastrada com sucesso!", salva.ID), false)
	p.modal.OnDismiss = func() {
		if p.onSalvo != nil {
			p.onSalvo()
		}
	}
}
