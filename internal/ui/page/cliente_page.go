package page

import (
	"context"
	"fmt"

	"gioui.org/layout"
	"gioui.org/unit"
	gwidget "gioui.org/widget"
	"gioui.org/widget/material"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
	"tech.jefersonms/ducarmolocacoes/internal/ui/widget"
)

type ClientePage struct {
	repo        *sqlite.ClienteRepository
	modal       *widget.Modal
	clientes    []domain.Cliente
	buscaEd     gwidget.Editor
	btnBuscar   gwidget.Clickable
	btnNovo     gwidget.Clickable
	list        layout.List

	// Campos do mini-formulário de cadastro rápido
	showForm    bool
	nomeEd      gwidget.Editor
	cpfEd       gwidget.Editor
	rgEd        gwidget.Editor
	telEd       gwidget.Editor
	endEd       gwidget.Editor
	btnSalvar   gwidget.Clickable
	btnFechar   gwidget.Clickable
}

func NewClientePage(repo *sqlite.ClienteRepository, modal *widget.Modal) *ClientePage {
	p := &ClientePage{
		repo:  repo,
		modal: modal,
	}
	p.list.Axis = layout.Vertical
	p.Recarregar()
	return p
}

func (p *ClientePage) Recarregar() {
	termo := p.buscaEd.Text()
	clis, err := p.repo.Listar(context.Background(), termo)
	if err == nil {
		p.clientes = clis
	}
}

func (p *ClientePage) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
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
		p.salvarCliente()
	}

	return layout.UniformInset(unit.Dp(20)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
		return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
			// Header
			layout.Rigid(func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.H5(th.Theme, "Clientes & Locatários")
								lbl.Color = theme.ColorText
								return lbl.Layout(gtx)
							}),
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.Caption(th.Theme, "Busca instantânea FTS5 por nome, CPF ou telefone")
								lbl.Color = theme.ColorTextMuted
								return lbl.Layout(gtx)
							}),
						)
					}),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						btn := material.Button(th.Theme, &p.btnNovo, "+ Novo Cliente")
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
								ed := material.Editor(th.Theme, &p.buscaEd, "Buscar cliente por nome ou CPF...")
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

			// Lista de Clientes
			layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
				return p.list.Layout(gtx, len(p.clientes), func(gtx layout.Context, index int) layout.Dimensions {
					cli := &p.clientes[index]
					card := widget.NewCard()
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
								return layout.UniformInset(unit.Dp(12)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
									return layout.Flex{Alignment: layout.Middle, Spacing: layout.SpaceBetween}.Layout(gtx,
										layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
											return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
												layout.Rigid(func(gtx layout.Context) layout.Dimensions {
													lbl := material.Body1(th.Theme, cli.Nome)
													lbl.Color = theme.ColorText
													return lbl.Layout(gtx)
												}),
												layout.Rigid(func(gtx layout.Context) layout.Dimensions {
													lbl := material.Caption(th.Theme, fmt.Sprintf("CPF: %s | Fone: %s", cli.CPF, cli.Telefone1))
													lbl.Color = theme.ColorTextMuted
													return lbl.Layout(gtx)
												}),
											)
										}),
										layout.Rigid(func(gtx layout.Context) layout.Dimensions {
											lbl := material.Caption(th.Theme, fmt.Sprintf("%s - %s", cli.Endereco, cli.Cidade))
											lbl.Color = theme.ColorTextMuted
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

func (p *ClientePage) layoutFormCadastro(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	card := widget.NewCard()
	return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
		layout.Rigid(func(gtx layout.Context) layout.Dimensions {
			return card.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				return layout.UniformInset(unit.Dp(16)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
					return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							lbl := material.Subtitle1(th.Theme, "Cadastrar Novo Cliente")
							lbl.Color = theme.ColorPrimary
							return lbl.Layout(gtx)
						}),
						layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{}.Layout(gtx,
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.nomeEd, "Nome Completo")
									return ed.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(10)}.Layout),
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.cpfEd, "CPF (000.000.000-00)")
									return ed.Layout(gtx)
								}),
							)
						}),
						layout.Rigid(layout.Spacer{Height: unit.Dp(8)}.Layout),

						layout.Rigid(func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{}.Layout(gtx,
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.telEd, "Telefone Principal")
									return ed.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Width: unit.Dp(10)}.Layout),
								layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
									ed := material.Editor(th.Theme, &p.endEd, "Endereço Residencial")
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
									btn := material.Button(th.Theme, &p.btnSalvar, "Salvar Cliente")
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

func (p *ClientePage) salvarCliente() {
	nome := p.nomeEd.Text()
	cpf := p.cpfEd.Text()
	if nome == "" || cpf == "" {
		p.modal.Show("Campos Obrigatórios", "Nome e CPF são obrigatórios para cadastro.", true)
		return
	}

	cli := &domain.Cliente{
		Nome:      nome,
		CPF:       cpf,
		Telefone1: p.telEd.Text(),
		Endereco:  p.endEd.Text(),
		Cidade:    "Bocaiúva do Sul",
	}

	_, err := p.repo.Criar(context.Background(), cli)
	if err != nil {
		p.modal.Show("Erro ao Salvar", err.Error(), true)
		return
	}

	p.showForm = false
	p.nomeEd.SetText("")
	p.cpfEd.SetText("")
	p.telEd.SetText("")
	p.endEd.SetText("")
	p.Recarregar()
}
