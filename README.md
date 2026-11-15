# Du Carmo Locações - Desktop (Go + Gio UI + SQLite)

> Aplicação Desktop nativa em Go, construída com o framework gráfico **Gio UI** seguindo a arquitetura e padrões visuais do **Cryptopower**, banco de dados embutido **SQLite** (sem CGO, compatível com 32 bits e 64 bits) e gerador de contratos em PDF.

---

## 🏛️ Arquitetura do Sistema

```
app-desktop/
├── cmd/
│   ├── ducarmolocacoes/       # Entrypoint da Aplicação Desktop (Janela Gio UI)
│   └── migrator/             # Ferramenta CLI de migração de dados e testes
├── internal/
│   ├── core/                 # Camada de Domínio Puro (Regras de Negócio Inegociáveis)
│   │   ├── domain/           # Entidades, tipos monetários exatos (Dinheiro em centavos), máquinas de estado
│   │   ├── agenda/           # Verificador de colisão de trajes na janela de 7 dias [evento-3, evento+4]
│   │   ├── contrato/         # Motor de substituição de TAGs e gerador de PDF oficial para impressão
│   │   └── locacao/          # Serviços de reserva, entrega, devolução e regras de sinal (mínimo 20%)
│   ├── storage/
│   │   └── sqlite/           # Persistência local SQLite Go puro (modernc.org/sqlite), schema DDL e FTS5
│   └── ui/                   # Interface Gráfica Desktop (Immediate Mode)
│       ├── theme/            # Paleta Dark Cryptopower (#0a0f1d, #1e293b, #38bdf8, #10b981)
│       ├── widget/           # Componentes: Sidebar de navegação, cards com borda, modais de colisão/alerta
│       ├── page/             # Telas: Lista de Locações com Abas, Formulário Split de Locação, Clientes e Trajes
│       └── app.go            # Loop principal de eventos gráficos da janela
└── ducarmolocacoes.db        # Banco de dados SQLite local populado
```

---

## 🎯 Regras de Negócio e Invariantes Implementadas

1. **Sinal Obrigatório de 20%**:
   - `valor_sinal >= 0.20 * valor_total` para que a reserva seja confirmada. Se inferior, a aplicação exibe bloqueio imediato via modal.
2. **Cálculo Forçado de Saldo**:
   - `valor_saldo = valor_total - valor_sinal - valor_adiantado`. Sem arredondamentos ou imprecisões de ponto flutuante (armazenado em inteiros de centavos `type Dinheiro int64`).
3. **Proteção de Agenda de 7 Dias**:
   - Nenhum traje pode ser reservado para outra locação caso a data do evento colida no intervalo `[data_evento - 3 dias, data_evento + 4 dias]`.
4. **Quitação Obrigatória na Retirada**:
   - A transição para a situação `2 (Trajes Entregues)` é terminantemente bloqueada enquanto houver `valor_saldo > 0`.
5. **Busca Instantânea Local**:
   - Pesquisa full-text em tempo real via tabela virtual SQLite FTS5 para clientes e trajes.

---

## 🔨 Como Compilar

### 1. Compilação para Linux 64-bits (Nativo)
O executável Linux de 64 bits é gerado pronto para uso na sua distribuição (X11 / Wayland):
```bash
# Executável gerado na raiz de app-desktop/:
./ducarmolocacoes_linux64
```
Para recompilar a qualquer momento:
```bash
PKG_CONFIG_PATH=/tmp/sysroot/usr/lib/x86_64-linux-gnu/pkgconfig:/tmp/sysroot/usr/share/pkgconfig \
CGO_CFLAGS="-I/tmp/sysroot/usr/include" \
CGO_LDFLAGS="-L/tmp/sysroot/usr/lib/x86_64-linux-gnu" \
go build -o ducarmolocacoes_linux64 ./cmd/ducarmolocacoes
```

### 2. Compilação para Windows 32-bits (x86 / i386)
Compatível com computadores antigos e sistemas operacionais legados de 32 bits:
```bash
CGO_ENABLED=0 GOOS=windows GOARCH=386 go build -o ducarmolocacoes_win32.exe ./cmd/ducarmolocacoes
```

### 3. Compilação para Windows 64-bits (x64 / amd64)
```bash
CGO_ENABLED=0 GOOS=windows GOARCH=amd64 go build -o ducarmolocacoes_win64.exe ./cmd/ducarmolocacoes
```

### 4. Compilação da Ferramenta de Migração CLI (Migrator)
```bash
# Linux:
go build -o migrator_linux ./cmd/migrator

# Windows 32-bits:
CGO_ENABLED=0 GOOS=windows GOARCH=386 go build -o migrator_win32.exe ./cmd/migrator
```

---

## 🚀 Executando a Aplicação e Testes

### Executando os Testes Automatizados de Domínio e Banco:
```bash
go test -v ./internal/core/... ./internal/storage/...
```

### Inicializando o Banco com Dados de Teste:
```bash
./migrator_linux -db ducarmolocacoes.db -seed
```
Isso criará automaticamente o banco `ducarmolocacoes.db` com o catálogo inicial de vestidos, ternos, tipos de evento e clientes.
