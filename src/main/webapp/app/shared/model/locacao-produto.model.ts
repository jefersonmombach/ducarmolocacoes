import { IProduto } from 'app/shared/model/produto.model';
import { ILocacao } from 'app/shared/model/locacao.model';
import { ICliente } from 'app/shared/model/cliente.model';

export interface ILocacaoProduto {
    id?: number;
    quantidade?: number;
    valorUnitario?: number;
    valorTotal?: number;
    produtoId?: number;
    locacaoId?: number;
    clienteId?: number;
}

export class LocacaoProduto implements ILocacaoProduto {
    constructor(
        public id?: number,
        public quantidade?: number,
        public valorUnitario?: number,
        public valorTotal?: number,
        public produtoId?: number,
        public locacaoId?: number,
        public clienteId?: number
    ) {}
}
