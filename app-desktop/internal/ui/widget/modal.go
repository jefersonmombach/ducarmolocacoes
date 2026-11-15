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

type Modal struct {
	Visible   bool
	Title     string
	Message   string
	IsError   bool
	BtnOK     gwidget.Clickable
	OnDismiss func()
}

func (m *Modal) Show(title, message string, isError bool) {
	m.Title = title
	m.Message = message
	m.IsError = isError
	m.Visible = true
}

func (m *Modal) Dismiss() {
	m.Visible = false
	if m.OnDismiss != nil {
		m.OnDismiss()
	}
}

func (m *Modal) Layout(gtx layout.Context, th *theme.Theme) layout.Dimensions {
	if !m.Visible {
		return layout.Dimensions{}
	}

	if m.BtnOK.Clicked(gtx) {
		m.Dismiss()
	}

	// Backdrop escuro semitransparente
	return layout.Stack{}.Layout(gtx,
		layout.Expanded(func(gtx layout.Context) layout.Dimensions {
			r := image.Rectangle{Max: gtx.Constraints.Min}
			paint.FillShape(gtx.Ops, color.NRGBA{R: 0, G: 0, B: 0, A: 180}, clip.Rect(r).Op())
			return layout.Dimensions{Size: gtx.Constraints.Min}
		}),
		layout.Stacked(func(gtx layout.Context) layout.Dimensions {
			return layout.Center.Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				gtx.Constraints.Min.X = gtx.Dp(unit.Dp(420))
				gtx.Constraints.Max.X = gtx.Dp(unit.Dp(480))

				return layout.Stack{}.Layout(gtx,
					layout.Expanded(func(gtx layout.Context) layout.Dimensions {
						r := image.Rectangle{Max: gtx.Constraints.Min}
						rr := clip.RRect{
							Rect: r,
							SE:   gtx.Dp(10), SW: gtx.Dp(10), NW: gtx.Dp(10), NE: gtx.Dp(10),
						}
						paint.FillShape(gtx.Ops, theme.ColorSurface, rr.Op(gtx.Ops))
						return layout.Dimensions{Size: gtx.Constraints.Min}
					}),
					layout.Stacked(func(gtx layout.Context) layout.Dimensions {
						return layout.UniformInset(unit.Dp(24)).Layout(gtx, func(gtx layout.Context) layout.Dimensions {
							return layout.Flex{Axis: layout.Vertical}.Layout(gtx,
								// Título do Modal
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									lbl := material.H6(th.Theme, m.Title)
									if m.IsError {
										lbl.Color = theme.ColorDanger
									} else {
										lbl.Color = theme.ColorPrimary
									}
									return lbl.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Height: unit.Dp(12)}.Layout),

								// Mensagem explicativa
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									lbl := material.Body1(th.Theme, m.Message)
									lbl.Color = theme.ColorText
									return lbl.Layout(gtx)
								}),
								layout.Rigid(layout.Spacer{Height: unit.Dp(20)}.Layout),

								// Botão OK / Fechar
								layout.Rigid(func(gtx layout.Context) layout.Dimensions {
									btn := material.Button(th.Theme, &m.BtnOK, "Entendido")
									btn.Background = theme.ColorPrimaryDark
									btn.Color = theme.ColorText
									return btn.Layout(gtx)
								}),
							)
						})
					}),
				)
			})
		}),
	)
}
