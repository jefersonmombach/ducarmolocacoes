import { Moment } from 'moment';
import { ILocacao } from 'app/shared/model//locacao.model';
import { ILocacaoProduto } from 'app/shared/model//locacao-produto.model';

export interface ICliente {
    id?: number;
    nome?: string;
    nascimento?: Moment;
    cpf?: string;
    rg?: string;
    telefoneResidencial?: string;
    telefoneCelular?: string;
    telefoneRecado?: string;
    endereco?: string;
    locacoes?: ILocacao[];
    produtos?: ILocacaoProduto[];
}

export class Cliente implements ICliente {
    constructor(
        public id?: number,
        public nome?: string,
        public nascimento?: Moment,
        public cpf?: string,
        public rg?: string,
        public telefoneResidencial?: string,
        public telefoneCelular?: string,
        public telefoneRecado?: string,
        public endereco?: string,
        public locacoes?: ILocacao[],
        public produtos?: ILocacaoProduto[]
    ) {}
}
