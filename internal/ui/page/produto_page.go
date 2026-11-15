package page

import (
	"context"

	"gioui.org/layout"
	"gioui.org/unit"
	gwidget "gioui.org/widget"
	"gioui.org/widget/material"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
	"tech.jefersonms/ducarmolocacoes/internal/ui/widget"
)

type ProdutoPage struct {
	repo       *sqlite.ProdutoRepository
	modal      *widget.Modal
	produtos   []domain.Produto
	buscaEd    gwidget.Editor
	btnBuscar  gwidget.Clickable
	btnNovo    gwidget.Clickable
	list       layout.List

	// Formulário rápido
	showForm   bool
	descEd     gwidget.Editor
	valorEd    gwidget.Editor
	btnSalvar  gwidget.Clickable
	btnFechar  gwidget.Clickable
}

func NewProdutoPage(repo *sqlite.ProdutoRepository, modal *widget.Modal) *ProdutoPage {
	p := &ProdutoPage{
		repo:  repo,
		modal: modal,
	}
	p.list.Axis = layout.Vertical
	p.Recarregar()
	return p
}

func (p *ProdutoPage) Recarregar() {
	termo := p.buscaEd.Text()
	prods, err := p.repo.Listar(context.Background(), true, termo)
	if err == nil {
		p.produtos = prods
	}
}

func (p *ProdutoPage) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	if p.btnBuscar.Clicked(gtx) {
		p.Recarregar()
	}

	if p.btnNovo.Clicked(gtx) {
		p.showForm = true
	}

	if p.btnFechar.Clicked(gtx) {
		p.showForm = false
	}

	if p.btnSalvar.Clicked(gtx) {
		p.salvarProduto()
	}

	return layout.UniformInset(unit.Dp(20)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
			// Header
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.H5(th.Theme, "Catálogo de Trajes & Vestuário")
								lbl.Color = theme.ColorText
								return lbl.Layout(gtx)
							}),
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.Caption(th.Theme, "Controle de acervo de vestidos de noiva, debutante, ternos e acessórios")
								lbl.Color = theme.ColorTextMuted
								return lbl.Layout(gtx)
							}),
						)
					}),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnNovo, "+ Novo Traje")
						btn.Background = theme.ColorPrimaryDark
						btn.Color = theme.ColorText
						return btn.Layout(gtx)
					}),
				)
			}),
			layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),

			// Mini-formulário de cadastro se ativo
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				if !p.showForm {
					return layout.Dimensions{}
				}
				return p.layoutFormCadastro(gtx, th)
			}),

			// Barra de Busca
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				card := widget.NewCard()
				return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
					return layout.UniformInset(unit.Dp(8)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Alignment: layout.Middle}.Layout(gtx,
							layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
								ed := material.Editor(th.Theme, &p.buscaEd, "Buscar traje por descrição...")
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

			// Lista de Produtos
			layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
				return p.list.Layout(gtx, len(p.produtos), func(gtx layout.Context, index int) layout.Dimensions {
					prod := &p.produtos[index]
					card := widget.NewCard()
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
								return layout.UniformInset(unit.Dp(12)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
									return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
										layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
											lbl := material.Body1(th.Theme, prod.Descricao)
											lbl.Color = theme.ColorText
											return lbl.Layout(gtx)
										}),
										layout.Rigid(func(gtx layout.Context) layout.Dimensions {
											lbl := material.Subtitle2(th.Theme, prod.ValorBase.FormatBR())
											lbl.Color = theme.ColorPrimary
											return lbl.Layout(gtx)
										}),
									)
								})
							})
						}),
						layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),
					)
				})
			}),
		)
	})
}

func (p *ProdutoPage) layoutFormCadastro(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	card := widget.NewCard()
	return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				return layout.UniformInset(unit.Dp(16)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							lbl := material.Subtitle1(th.Theme, "Cadastrar Novo Traje")
							lbl.Color = theme.ColorPrimary
							return lbl.Layout(gtx)
						}),
						layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{}.Layout(gtx,
								layout.Flexed(2, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.descEd, "Descrição do Traje (ex: Vestido Noiva)")
									return ed.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(10)}.Layout),
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.valorEd, "Valor Base (ex: 250,00)")
									return ed.Layout(gtx)
								}),
							)
						}),
						layout.Rigid(layout.Spacer{Height: unit.Dp(12)}.Layout),

						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Spacing: layout.SpaceEnd}.Layout(gtx,
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									btn := material.Button(th.Theme, &p.btnFechar, "Cancelar")
									btn.Background = theme.ColorSurfaceAlt
									return btn.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(8)}.Layout),
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									btn := material.Button(th.Theme, &p.btnSalvar, "Salvar Traje")
									btn.Background = theme.ColorSuccess
									return btn.Layout(gtx)
								}),
							)
						}),
					)
				})
			})
		}),
		layout.Rigid(layout.Spacer{Height: unit.Dp(16)}.Layout),
	)
}

func (p *ProdutoPage) salvarProduto() {
	desc := p.descEd.Text()
	if desc == "" {
		p.modal.Show("Campo Obrigatório", "A descrição do traje é obrigatória.", true)
		return
	}

	prod := &domain.Produto{
		Descricao: desc,
		ValorBase: parseDinheiroBR(p.valorEd.Text()),
		Ativo:     true,
	}

	_, err := p.repo.Criar(context.Background(), prod)
	if err != nil {
		p.modal.Show("Erro ao Salvar", err.Error(), true)
		return
	}

	p.showForm = false
	p.descEd.SetText("")
	p.valorEd.SetText("")
	p.Recarregar()
}
