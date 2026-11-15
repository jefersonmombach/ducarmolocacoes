package sqlite

const SchemaDDL = `
PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS clientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    cpf TEXT NOT NULL UNIQUE,
    rg TEXT,
    telefone1 TEXT NOT NULL,
    telefone2 TEXT,
    endereco TEXT,
    bairro TEXT,
    cidade TEXT DEFAULT 'Bocaiúva do Sul',
    cep TEXT,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS produtos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL,
    valor_base INTEGER NOT NULL, -- Valor em centavos
    ativo INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS tipo_eventos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    descricao TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS locacoes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    cliente_id INTEGER NOT NULL REFERENCES clientes(id) ON DELETE RESTRICT,
    tipo_evento_id INTEGER REFERENCES tipo_eventos(id) ON DELETE SET NULL,
    data_contratacao DATE NOT NULL,
    data_evento DATE NOT NULL,
    data_entr_prev DATE NOT NULL,
    data_dev_prev DATE NOT NULL,
    data_entr_real DATETIME,
    data_dev_real DATETIME,
    valor_total INTEGER NOT NULL,      -- Em centavos
    valor_sinal INTEGER NOT NULL,      -- Em centavos
    valor_adiantado INTEGER DEFAULT 0, -- Em centavos
    valor_saldo INTEGER NOT NULL,      -- Em centavos
    situacao INTEGER NOT NULL DEFAULT 0,
    html_contrato TEXT,
    observacoes TEXT,
    criado_em DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS locacao_produtos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    locacao_id INTEGER NOT NULL REFERENCES locacoes(id) ON DELETE CASCADE,
    produto_id INTEGER NOT NULL REFERENCES produtos(id) ON DELETE RESTRICT,
    quantidade INTEGER NOT NULL DEFAULT 1,
    valor_unit INTEGER NOT NULL,
    valor_total INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS usuarios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    login TEXT NOT NULL UNIQUE,
    nome TEXT NOT NULL,
    senha_hash TEXT NOT NULL,
    perfil TEXT NOT NULL DEFAULT 'OPERADOR'
);

CREATE INDEX IF NOT EXISTS idx_locacoes_data_evento ON locacoes(data_evento);
CREATE INDEX IF NOT EXISTS idx_locacoes_cliente ON locacoes(cliente_id);
CREATE INDEX IF NOT EXISTS idx_locacoes_situacao ON locacoes(situacao);
CREATE INDEX IF NOT EXISTS idx_locacao_produtos_produto ON locacao_produtos(produto_id);
CREATE INDEX IF NOT EXISTS idx_locacao_produtos_locacao ON locacao_produtos(locacao_id);

CREATE VIRTUAL TABLE IF NOT EXISTS clientes_fts USING fts5(
    nome, 
    cpf, 
    telefone1, 
    content='clientes', 
    content_rowid='id'
);

CREATE VIRTUAL TABLE IF NOT EXISTS produtos_fts USING fts5(
    descricao, 
    content='produtos', 
    content_rowid='id'
);
`

const SeedsSQL = `
INSERT OR IGNORE INTO tipo_eventos (id, descricao) VALUES 
(1, 'Casamento'),
(2, 'Formatura'),
(3, '15 Anos / Debutante'),
(4, 'Bodas'),
(5, 'Gala / Festa');

INSERT OR IGNORE INTO produtos (id, descricao, valor_base, ativo) VALUES 
(1, 'Vestido de Noiva Imperial Rendado', 35000, 1),
(2, 'Terno Slim Fit Preto Completo', 15000, 1),
(3, 'Smoking Tradicional com Faixa', 18000, 1),
(4, 'Vestido Debutante Princesa Azul Royal', 25000, 1),
(5, 'Vestido Madrinha Longo Rosê', 14000, 1),
(6, 'Fraque Clássico Cinza Chumbo', 20000, 1);

INSERT OR IGNORE INTO clientes (id, nome, cpf, rg, telefone1, telefone2, endereco, bairro, cidade, cep) VALUES
(1, 'Maria Fernanda de Souza', '111.222.333-44', '1234567-8', '(41) 99888-1122', '(41) 3675-1234', 'Rua São Paulo, 120', 'Centro', 'Bocaiúva do Sul', '83450-000'),
(2, 'Carlos Eduardo Ribeiro', '555.666.777-88', '8765432-1', '(41) 99777-3344', '', 'Av. Getúlio Vargas, 450', 'Jardim das Flores', 'Bocaiúva do Sul', '83450-000');
`
