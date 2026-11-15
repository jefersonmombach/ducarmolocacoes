package ui

import (
	"log"

	"gioui.org/app"
	"gioui.org/io/key"
	"gioui.org/layout"
	"gioui.org/op"
	"gioui.org/unit"
	"tech.jefersonms/ducarmolocacoes/internal/core/domain"
	"tech.jefersonms/ducarmolocacoes/internal/core/locacao"
	"tech.jefersonms/ducarmolocacoes/internal/storage/sqlite"
	"tech.jefersonms/ducarmolocacoes/internal/ui/page"
	"tech.jefersonms/ducarmolocacoes/internal/ui/theme"
	"tech.jefersonms/ducarmolocacoes/internal/ui/widget"
)

type App struct {
	window       *app.Window
	theme        *theme.Theme
	sidebar      *widget.Sidebar
	modal        *widget.Modal
	activeRoute  widget.Route

	// Pages
	locacaoListPage *page.LocacaoListPage
	locacaoFormPage *page.LocacaoFormPage
	clientePage     *page.ClientePage
	produtoPage     *page.ProdutoPage
}

func NewApp(
	svc *locacao.Service,
	cliRepo *sqlite.ClienteRepository,
	prodRepo *sqlite.ProdutoRepository,
) *App {
	a := &App{
		theme:       theme.NewTheme(),
		modal:       &widget.Modal{},
		activeRoute: widget.RouteLocacoes,
	}

	a.sidebar = widget.NewSidebar(func(r widget.Route) {
		a.activeRoute = r
		if r == widget.RouteLocacoes {
			a.locacaoListPage.Recarregar()
		} else if r == widget.RouteClientes {
			a.clientePage.Recarregar()
		} else if r == widget.RouteProdutos {
			a.produtoPage.Recarregar()
		}
	})

	a.locacaoListPage = page.NewLocacaoListPage(
		svc,
		func() {
			a.locacaoFormPage.Inicializar()
			a.activeRoute = widget.RouteNovoLocacao
		},
		func(loc *domain.Locacao) {
			// Pode abrir detalhes se necessário
		},
		a.modal,
	)

	a.locacaoFormPage = page.NewLocacaoFormPage(
		svc,
		cliRepo,
		prodRepo,
		a.modal,
		func() {
			a.activeRoute = widget.RouteLocacoes
			a.locacaoListPage.Recarregar()
		},
		func() {
			a.activeRoute = widget.RouteLocacoes
		},
	)

	a.clientePage = page.NewClientePage(cliRepo, a.modal)
	a.produtoPage = page.NewProdutoPage(prodRepo, a.modal)

	return a
}

func (a *App) Run() error {
	a.window = app.NewWindow(
		app.Title("Du Carmo Locações - Desktop"),
		app.Size(unit.Dp(1180), unit.Dp(780)),
		app.MinSize(unit.Dp(900), unit.Dp(600)),
	)

	var ops op.Ops
	for {
		switch e := a.window.NextEvent().(type) {
		case app.DestroyEvent:
			return e.Err
		case app.FrameEvent:
			gtx := app.NewContext(&ops, e)
			a.Layout(gtx)
			e.Frame(gtx.Ops)
		case key.Event:
			if e.Name == key.NameEscape && a.modal.Visible {
				a.modal.Dismiss()
			}
		}
	}
}

func (a *App) Layout(gtx layout.Context) layout.Dimensions {
	return layout.Stack{}.Layout(gtx,
		// Fundo escuro geral
		layout.Expanded(func(gtx layout.Context) layout.Dimensions {
			return widget.NewCard().Layout(gtx, func(gtx layout.Context) layout.Dimensions {
				return layout.Dimensions{Size: gtx.Constraints.Min}
			})
		}),
		// Conteúdo principal: Sidebar fixa + View ativa
		layout.Stacked(func(gtx layout.Context) layout.Dimensions {
			return layout.Flex{}.Layout(gtx,
				// Sidebar esquerda (largura fixa 220dp)
				layout.Rigid(func(gtx layout.Context) layout.Dimensions {
					gtx.Constraints.Min.X = gtx.Dp(unit.Dp(220))
					gtx.Constraints.Max.X = gtx.Dp(unit.Dp(220))
					return a.sidebar.Layout(gtx, a.theme)
				}),

				// Área de Trabalho / Página Ativa
				layout.Flexed(1, func(gtx layout.Context) layout.Dimensions {
					switch a.activeRoute {
					case widget.RouteLocacoes:
						return a.locacaoListPage.Layout(gtx, a.theme)
					case widget.RouteNovoLocacao:
						return a.locacaoFormPage.Layout(gtx, a.theme)
					case widget.RouteClientes:
						return a.clientePage.Layout(gtx, a.theme)
					case widget.RouteProdutos:
						return a.produtoPage.Layout(gtx, a.theme)
					default:
						return a.locacaoListPage.Layout(gtx, a.theme)
					}
				}),
			)
		}),
		// Modal de alerta ou erro em sobreposição
		layout.Stacked(func(gtx layout.Context) layout.Dimensions {
			return a.modal.Layout(gtx, a.theme)
		}),
	)
}

func RunApp(svc *locacao.Service, cliRepo *sqlite.ClienteRepository, prodRepo *sqlite.ProdutoRepository) {
	go func() {
		a := NewApp(svc, cliRepo, prodRepo)
		if err := a.Run(); err != nil {
			log.Fatalf("erro ao executar app desktop: %v", err)
		}
	}()
	app.Main()
}
