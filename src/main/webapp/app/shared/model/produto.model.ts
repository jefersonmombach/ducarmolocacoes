import { ILocacaoProduto } from 'app/shared/model//locacao-produto.model';

export interface IProduto {
    id?: number;
    descricao?: string;
    precoVenda?: number;
    precoCompra?: number;
    ativo?: boolean;
    locacoes?: ILocacaoProduto[];
}

export class Produto implements IProduto {
    constructor(
        public id?: number,
        public descricao?: string,
        public precoVenda?: number,
        public precoCompra?: number,
        public ativo?: boolean,
        public locacoes?: ILocacaoProduto[]
    ) {
        this.ativo = this.ativo || false;
    }
}
