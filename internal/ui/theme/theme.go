package theme

import (
	"image/color"

	"gioui.org/unit"
	"gioui.org/widget/material"
)

// Paleta de Cores Sóbria Dark (Cryptopower Pattern)
var (
	ColorBackground = color.NRGBA{R: 0x0a, G: 0x0f, B: 0x1d, A: 0xff} // #0a0f1d
	ColorSidebar    = color.NRGBA{R: 0x0f, G: 0x17, B: 0x2a, A: 0xff} // #0f172a (Slate-900)
	ColorSurface    = color.NRGBA{R: 0x1e, G: 0x29, B: 0x3b, A: 0xff} // #1e293b (Slate-800)
	ColorSurfaceAlt = color.NRGBA{R: 0x33, G: 0x41, B: 0x55, A: 0xff} // #334155 (Slate-700)
	ColorBorder     = color.NRGBA{R: 0x33, G: 0x41, B: 0x55, A: 0xff} // #334155
	ColorPrimary    = color.NRGBA{R: 0x38, G: 0xbd, B: 0xf8, A: 0xff} // #38bdf8 (Sky-400)
	ColorPrimaryDark= color.NRGBA{R: 0x02, G: 0x84, B: 0xc7, A: 0xff} // #0284c7 (Sky-600)
	ColorSuccess    = color.NRGBA{R: 0x10, G: 0xb9, B: 0x81, A: 0xff} // #10b981 (Emerald-500)
	ColorWarning    = color.NRGBA{R: 0xf5, G: 0x9e, B: 0x0b, A: 0xff} // #f59e0b (Amber-500)
	ColorDanger     = color.NRGBA{R: 0xef, G: 0x44, B: 0x44, A: 0xff} // #ef4444 (Red-500)
	ColorText       = color.NRGBA{R: 0xf8, G: 0xfa, B: 0xfc, A: 0xff} // #f8fafc (Slate-50)
	ColorTextMuted  = color.NRGBA{R: 0x94, G: 0xa3, B: 0xb8, A: 0xff} // #94a3b8 (Slate-400)
)

type Theme struct {
	*material.Theme
}

func NewTheme() *Theme {
	th := material.NewTheme()
	th.Palette.Bg = ColorBackground
	th.Palette.Fg = ColorText
	th.Palette.ContrastBg = ColorPrimary
	th.Palette.ContrastFg = color.NRGBA{R: 0x0a, G: 0x0f, B: 0x1d, A: 0xff}

	return &Theme{Theme: th}
}

// Dimensões e espaçamentos padrão
var (
	SpacingSmall  = unit.Dp(4)
	SpacingMedium = unit.Dp(8)
	SpacingLarge  = unit.Dp(16)
	SpacingXLarge = unit.Dp(24)
)
