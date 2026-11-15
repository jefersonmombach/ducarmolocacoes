package page

import (
	"context"
	"fmt"
	"image"
	"image/color"
	"os/exec"
	"runtime"

	"gioui.org/layout"
	"gioui.org/op/clip"
	"gioui.org/op/paint"
	"gioui.org/unit"
	gwidget "gioui.org/widget"
	"gioui.org/widget/material"
	"tech.jefersonms/ducarmolocacoes/internal/core/contrato"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/core/locacao"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
	"tech.jefersonms/ducarmolocacoes/internal/ui/widget"
)

type LocacaoRowActions struct {
	BtnAvancar   gwidget.Clickable
	BtnPDF       gwidget.Clickable
	BtnDetalhes  gwidget.Clickable
}

type LocacaoListPage struct {
	svc          *locacao.Service
	locacoes     []domain.Locacao
	rowActions   []LocacaoRowActions
	filtroStatus *domain.SituacaoLocacao
	buscaEditor  gwidget.Editor
	btnNova      gwidget.Clickable
	btnBuscar    gwidget.Clickable
	tabs         []gwidget.Clickable
	tabLabels    []string
	activeTab    int
	list         layout.List
	modal        *widget.Modal
	onNova       func()
	onVer        func(loc *domain.Locacao)
}

func NewLocacaoListPage(svc *locacao.Service, onNova func(), onVer func(loc *domain.Locacao), modal *widget.Modal) *LocacaoListPage {
	p := &LocacaoListPage{
		svc:       svc,
		modal:     modal,
		onNova:    onNova,
		onVer:     onVer,
		tabLabels: []string{"Todas", "Novas", "Contrato Emitido", "Entregues", "Devolvidos"},
		tabs:      make([]gwidget.Clickable, 5),
		activeTab: 0,
	}
	p.list.Axis = layout.Vertical
	p.Recarregar()
	return p
}

func (p *LocacaoListPage) Recarregar() {
	var sit *domain.SituacaoLocacao
	switch p.activeTab {
	case 1:
		s := domain.SituacaoNovo
		sit = &s
	case 2:
		s := domain.SituacaoContratoEmitido
		sit = &s
	case 3:
		s := domain.SituacaoTrajesEntregues
		sit = &s
	case 4:
		s := domain.SituacaoTrajesDevolvidos
		sit = &s
	}
	p.filtroStatus = sit

	busca := p.buscaEditor.Text()
	locs, err := p.svc.Listar(context.Background(), p.filtroStatus, busca)
	if err == nil {
		p.locacoes = locs
		p.rowActions = make([]LocacaoRowActions, len(locs))
	}
}

func (p *LocacaoListPage) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	if p.btnNova.Clicked(gtx) {
		if p.onNova != nil {
			p.onNova()
		}
	}

	if p.btnBuscar.Clicked(gtx) {
		p.Recarregar()
	}

	for i := range p.tabs {
		if p.tabs[i].Clicked(gtx) {
			p.activeTab = i
			p.Recarregar()
		}
	}

	// Trata ações nas linhas
	for i := range p.locacoes {
		loc := &p.locacoes[i]
		actions := &p.rowActions[i]

		if actions.BtnAvancar.Clicked(gtx) {
			p.executarTransicao(loc)
		}

		if actions.BtnPDF.Clicked(gtx) {
			p.gerarEImprimirPDF(loc)
		}

		if actions.BtnDetalhes.Clicked(gtx) {
			if p.onVer != nil {
				p.onVer(loc)
			}
		}
	}

	return layout.UniformInset(unit.Dp(20)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
			// Cabeçalho da Página
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.H5(th.Theme, "Locações de Trajes")
								lbl.Color = theme.ColorText
								return lbl.Layout(gtx)
							}),
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.Caption(th.Theme, "Gerenciamento de contratos, reservas e controle de entrega/devolução")
								lbl.Color = theme.ColorTextMuted
								return lbl.Layout(gtx)
							}),
						)
					}),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnNova, "+ Nova Locação (F2)")
						btn.Background = theme.ColorPrimaryDark
						btn.Color = theme.ColorText
						return btn.Layout(gtx)
					}),
				)
			}),
			layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

			// Barra de Filtros por Abas (Tabs)
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				var tabWidgets []layout.FlexChild
				for i := range p.tabLabels {
					idx := i
					tabWidgets = append(tabWidgets, layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := &p.tabs[idx]
						isSelected := (p.activeTab == idx)

						return btn.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
							bg := color.NRGBA{}
							if isSelected {
								bg = theme.ColorSurfaceAlt
							}
							return layout.Stack{}.Layout(gtx,
								layout.Expanded(func(gtx layout.Context) layout.Dimensions {
									r := image.Rectangle{Max: gtx.Constraints.Min}
									rr := clip.RRect{Rect: r, SE: gtx.Dp(6), SW: gtx.Dp(6), NW: gtx.Dp(6), NE: gtx.Dp(6)}
									paint.FillShape(gtx.Ops, bg, rr.Op(gtx.Ops))
									return layout.Dimensions{Size: gtx.Constraints.Min}
								}),
								layout.Stacked(func(gtx layout.Context) layout.Dimensions {
									return layout.UniformInset(unit.Dp(8)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
										lbl := material.Body2(th.Theme, p.tabLabels[idx])
										if isSelected {
											lbl.Color = theme.ColorPrimary
										} else {
											lbl.Color = theme.ColorTextMuted
										}
										return lbl.Layout(gtx)
									})
								}),
							)
						})
					}))
					tabWidgets = append(tabWidgets, layout.Rigid(layout.Spacer{Width: unit.Dp(8)}.Layout))
				}
				return layout.Flex{}.Layout(gtx, tabWidgets...)
			}),
			layout.Rigid(layout.Spacer{Height: unit.Dp(14)}.Layout),

			// Campo de Busca
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				card := widget.NewCard()
				return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
					return layout.UniformInset(unit.Dp(8)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Alignment: layout.Middle}.Layout(gtx,
							layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
								ed := material.Editor(th.Theme, &p.buscaEditor, "Buscar por cliente ou número da locação...")
								ed.Color = theme.ColorText
								return ed.Layout(gtx)
							}),
							layout.Rigid(layout.Spacer{Width: unit.Dp(10)}.Layout),
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								btn := material.Button(th.Theme, &p.btnBuscar, "Buscar")
								btn.Background = theme.ColorSurfaceAlt
								btn.Color = theme.ColorText
								return btn.Layout(gtx)
							}),
						)
					})
				})
			}),
			layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

			// Lista de Locações
			layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
				if len(p.locacoes) == 0 {
					return layout.Center.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
						lbl := material.Body1(th.Theme, "Nenhuma locação encontrada para este filtro.")
						lbl.Color = theme.ColorTextMuted
						return lbl.Layout(gtx)
					})
				}

				return p.list.Layout(gtx, len(p.locacoes), func(gtx layout.Context, index int) layout.Dimensions {
					loc := &p.locacoes[index]
					actions := &p.rowActions[index]
					return p.layoutCardLocacao(gtx, th, loc, actions)
				})
			}),
		)
	})
}

func (p *LocacaoListPage) layoutCardLocacao(gtx layout.Context, th *theme.Theme, loc *domain.Locacao, actions *LocacaoRowActions) layout.Dimensions {
	card := widget.NewCard()
	return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				return layout.UniformInset(unit.Dp(14)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
					return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
						// Coluna 1: ID, Cliente, Evento
						layout.Flexed(1.2, func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									nomeCli := "Cliente não identificado"
									if loc.Cliente != nil {
										nomeCli = loc.Cliente.Nome
									}
									lbl := material.Body1(th.Theme, fmt.Sprintf("#%05d - %s", loc.ID, nomeCli))
									lbl.Color = theme.ColorText
									return lbl.Layout(gtx)
								}),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									lbl := material.Caption(th.Theme, fmt.Sprintf("Evento: %s | Retirada: %s | Devolução: %s",
										loc.DataEvento.Format("02/01/2006"),
										loc.DataEntrPrev.Format("02/01/2006"),
										loc.DataDevPrev.Format("02/01/2006"),
									))
									lbl.Color = theme.ColorTextMuted
									return lbl.Layout(gtx)
								}),
							)
						}),

						// Coluna 2: Valores
						layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									lbl := material.Body2(th.Theme, fmt.Sprintf("Total: %s  (Sinal: %s)", loc.ValorTotal.FormatBR(), loc.ValorSinal.FormatBR()))
									lbl.Color = theme.ColorText
									return lbl.Layout(gtx)
								}),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									corSaldo := theme.ColorSuccess
									if loc.ValorSaldo > 0 {
										corSaldo = theme.ColorDanger
									}
									lbl := material.Caption(th.Theme, fmt.Sprintf("Saldo Pendente: %s", loc.ValorSaldo.FormatBR()))
									lbl.Color = corSaldo
									return lbl.Layout(gtx)
								}),
							)
						}),

						// Coluna 3: Badge Situação
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return p.layoutBadgeSituacao(gtx, th, loc.Situacao)
						}),
						layout.Rigid(layout.Spacer{Width: unit.Dp(16)}.Layout),

						// Coluna 4: Botões de Ação
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Alignment: layout.Middle}.Layout(gtx,
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									return p.layoutBotaoTransicao(gtx, th, loc, actions)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(8)}.Layout),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									btn := material.Button(th.Theme, &actions.BtnPDF, "PDF 📄")
									btn.Background = theme.ColorSurfaceAlt
									btn.Color = theme.ColorText
									return btn.Layout(gtx)
								}),
							)
						}),
					)
				})
			})
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),
	)
}

func (p *LocacaoListPage) layoutBadgeSituacao(gtx layout.Context, th *theme.Theme, sit domain.SituacaoLocacao) layout.Dimensions {
	var bg color.NRGBA
	switch sit {
	case domain.SituacaoNovo:
		bg = theme.ColorWarning
	case domain.SituacaoContratoEmitido:
		bg = theme.ColorPrimaryDark
	case domain.SituacaoTrajesEntregues:
		bg = theme.ColorSuccess
	case domain.SituacaoTrajesDevolvidos:
		bg = theme.ColorSurfaceAlt
	default:
		bg = theme.ColorDanger
	}

	return layout.Stack{}.Layout(gtx,
		layout.Expanded(func(gtx layout.Context) layout.Dimensions {
			r := image.Rectangle{Max: gtx.Constraints.Min}
			rr := clip.RRect{Rect: r, SE: gtx.Dp(4), SW: gtx.Dp(4), NW: gtx.Dp(4), NE: gtx.Dp(4)}
			paint.FillShape(gtx.Ops, bg, rr.Op(gtx.Ops))
			return layout.Dimensions{Size: gtx.Constraints.Min}
		}),
		layout.Stacked(func(gtx layout.Context) layout.Dimensions {
			return layout.UniformInset(unit.Dp(6)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				lbl := material.Caption(th.Theme, sit.String())
				lbl.Color = theme.ColorText
				return lbl.Layout(gtx)
			})
		}),
	)
}

func (p *LocacaoListPage) layoutBotaoTransicao(gtx layout.Context, th *theme.Theme, loc *domain.Locacao, actions *LocacaoRowActions) layout.Dimensions {
	switch loc.Situacao {
	case domain.SituacaoNovo:
		btn := material.Button(th.Theme, &actions.BtnAvancar, "Emitir Contrato (1)")
		btn.Background = theme.ColorPrimaryDark
		return btn.Layout(gtx)
	case domain.SituacaoContratoEmitido:
		btn := material.Button(th.Theme, &actions.BtnAvancar, "Entregar Trajes (2)")
		btn.Background = theme.ColorSuccess
		return btn.Layout(gtx)
	case domain.SituacaoTrajesEntregues:
		btn := material.Button(th.Theme, &actions.BtnAvancar, "Receber Devolução (3)")
		btn.Background = theme.ColorSurfaceAlt
		return btn.Layout(gtx)
	default:
		return layout.Dimensions{}
	}
}

func (p *LocacaoListPage) executarTransicao(loc *domain.Locacao) {
	ctx := context.Background()
	var err error
	switch loc.Situacao {
	case domain.SituacaoNovo:
		_, err = p.svc.EmitirContrato(ctx, loc.ID)
	case domain.SituacaoContratoEmitido:
		_, err = p.svc.EntregarTrajes(ctx, loc.ID)
	case domain.SituacaoTrajesEntregues:
		_, err = p.svc.DevolverTrajes(ctx, loc.ID)
	}

	if err != nil {
		p.modal.Show("Atenção na Transição", err.Error(), true)
	} else {
		p.Recarregar()
	}
}

func (p *LocacaoListPage) gerarEImprimirPDF(loc *domain.Locacao) {
	completa, err := p.svc.ObterPorID(context.Background(), loc.ID)
	if err != nil {
		p.modal.Show("Erro ao Carregar Locação", err.Error(), true)
		return
	}

	pdfBytes, err := contrato.GerarPDF(completa)
	if err != nil {
		p.modal.Show("Erro ao Gerar PDF", err.Error(), true)
		return
	}

	nomeArquivo := fmt.Sprintf("/tmp/contrato_locacao_%05d.pdf", loc.ID)
	if runtime.GOOS == "windows" {
		nomeArquivo = fmt.Sprintf("contrato_locacao_%05d.pdf", loc.ID)
	}

	if err := writeFileBytes(nomeArquivo, pdfBytes); err != nil {
		p.modal.Show("Erro ao Gravar PDF", err.Error(), true)
		return
	}

	// Tenta abrir no visualizador padrão do sistema
	var cmd *exec.Cmd
	if runtime.GOOS == "windows" {
		cmd = exec.Command("cmd", "/c", "start", nomeArquivo)
	} else if runtime.GOOS == "darwin" {
		cmd = exec.Command("open", nomeArquivo)
	} else {
		cmd = exec.Command("xdg-open", nomeArquivo)
	}
	_ = cmd.Start()

	p.modal.Show("PDF Gerado com Sucesso", fmt.Sprintf("Contrato gravado em:\n%s", nomeArquivo), false)
}
