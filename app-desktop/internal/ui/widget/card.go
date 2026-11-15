package widget

import (
	"image"
	"image/color"

	"gioui.org/layout"
	"gioui.org/op/clip"
	"gioui.org/op/paint"
	"gioui.org/unit"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
)

type Card struct {
	Background   color.NRGBA
	BorderColor  color.NRGBA
	CornerRadius unit.Dp
}

func NewCard() Card {
	return Card{
		Background:   theme.ColorSurface,
		BorderColor:  theme.ColorBorder,
		CornerRadius: unit.Dp(8),
	}
}

func (c Card) Layout(gtx layout.Context, w layout.Widget) layout.Dimensions {
	return layout.Stack{}.Layout(gtx,
		layout.Expanded(func(gtx layout.Context) layout.Dimensions {
			r := image.Rectangle{Max: gtx.Constraints.Min}
			rr := clip.RRect{
				Rect: r,
				SE:   gtx.Dp(c.CornerRadius),
				SW:   gtx.Dp(c.CornerRadius),
				NW:   gtx.Dp(c.CornerRadius),
				NE:   gtx.Dp(c.CornerRadius),
			}
			paint.FillShape(gtx.Ops, c.Background, rr.Op(gtx.Ops))
			return layout.Dimensions{Size: gtx.Constraints.Min}
		}),
		layout.Stacked(w),
	)
}
