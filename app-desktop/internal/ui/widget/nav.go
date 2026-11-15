package widget

import (
	"image"
	"image/color"

	"gioui.org/layout"
	"gioui.org/op/clip"
	"gioui.org/op/paint"
	"gioui.org/unit"
	gwidget "gioui.org/widget"
	"gioui.org/widget/material"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
)

type Route int

const (
	RouteLocacoes Route = iota
	RouteClientes
	RouteProdutos
	RouteNovoLocacao
	RouteContratoView
)

type NavItem struct {
	Route Route
	Title string
	Icon  string
	Click gwidget.Clickable
}

type Sidebar struct {
	Items       []NavItem
	ActiveRoute Route
	OnNavigate  func(r Route)
}

func NewSidebar(onNavigate func(r Route)) *Sidebar {
	return &Sidebar{
		ActiveRoute: RouteLocacoes,
		OnNavigate:  onNavigate,
		Items: []NavItem{
			{Route: RouteLocacoes, Title: "Locações", Icon: "📋"},
			{Route: RouteClientes, Title: "Clientes", Icon: "👥"},
			{Route: RouteProdutos, Title: "Catálogo Trajes", Icon: "👗"},
		},
	}
}

func (s *Sidebar) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	// Trata cliques
	for i := range s.Items {
		if s.Items[i].Click.Clicked(gtx) {
			s.ActiveRoute = s.Items[i].Route
			if s.OnNavigate != nil {
				s.OnNavigate(s.ActiveRoute)
			}
		}
	}

	return layout.Stack{}.Layout(gtx,
		// Fundo da sidebar
		layout.Expanded(func(gtx layout.Context) layout.Dimensions {
			r := image.Rectangle{Max: gtx.Constraints.Min}
			paint.FillShape(gtx.Ops, theme.ColorSidebar, clip.Rect(r).Op())
			return layout.Dimensions{Size: gtx.Constraints.Min}
		}),
		layout.Stacked(func(gtx layout.Context) layout.Dimensions {
			return layout.UniformInset(unit.Dp(16)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
					// Header / Brand
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.H6(th.Theme, "Du Carmo")
								lbl.Color = theme.ColorPrimary
								return lbl.Layout(gtx)
							}),
							layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								lbl := material.Body2(th.Theme, "Locações de Trajes")
								lbl.Color = theme.ColorTextMuted
								return lbl.Layout(gtx)
							}),
							layout.Rigid(layout.Spacer{Height: unit.Dp(24)}.Layout),
						)
					}),

					// Itens de Menu
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						var flexChildren []layout.FlexChild
						for i := range s.Items {
							idx := i
							flexChildren = append(flexChildren, layout.Rigid(func(gtx layout.Context) layout.Dimensions {
								item := &s.Items[idx]
								isActive := (s.ActiveRoute == item.Route)

								return item.Click.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
									bg := color.NRGBA{}
									if isActive {
										bg = theme.ColorSurfaceAlt
									} else if item.Click.Hovered() {
										bg = theme.ColorSurface
									}

									return layout.Stack{}.Layout(gtx,
										layout.Expanded(func(gtx layout.Context) layout.Dimensions {
											r := image.Rectangle{Max: gtx.Constraints.Min}
											rr := clip.RRect{
												Rect: r,
												SE:   gtx.Dp(6), SW: gtx.Dp(6), NW: gtx.Dp(6), NE: gtx.Dp(6),
											}
											paint.FillShape(gtx.Ops, bg, rr.Op(gtx.Ops))
											return layout.Dimensions{Size: gtx.Constraints.Min}
										}),
										layout.Stacked(func(gtx layout.Context) layout.Dimensions {
											return layout.UniformInset(unit.Dp(10)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
												return layout.Flex{Alignment: layout.Middle}.Layout(gtx,
													layout.Rigid(func(gtx layout.Context) layout.Dimensions {
														iconLbl := material.Body1(th.Theme, item.Icon)
														return iconLbl.Layout(gtx)
													}),
													layout.Rigid(layout.Spacer{Width: unit.Dp(10)}.Layout),
													layout.Rigid(func(gtx layout.Context) layout.Dimensions {
														titleLbl := material.Body1(th.Theme, item.Title)
														if isActive {
															titleLbl.Color = theme.ColorPrimary
														} else {
															titleLbl.Color = theme.ColorText
														}
														return titleLbl.Layout(gtx)
													}),
												)
											})
										}),
									)
								})
							}))
							flexChildren = append(flexChildren, layout.Rigid(layout.Spacer{Height: unit.Dp(6)}.Layout))
						}
						return layout.Flex{Axis: layout.Vertical}.Layout(gtx, flexChildren...)
					}),

					// Rodapé da Sidebar
					layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
						return layout.Spacer{}.Layout(gtx)
					}),
					layout.Rigid(func(gtx layout.Context) layout.Dimensions {
						lbl := material.Caption(th.Theme, "v2.0 (Desktop Go)")
						lbl.Color = theme.ColorTextMuted
						return lbl.Layout(gtx)
					}),
				)
			})
		}),
	)
}
