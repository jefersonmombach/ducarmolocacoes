import { ILocacao } from 'app/shared/model//locacao.model';

export interface ITipoEvento {
    id?: number;
    descricao?: string;
    locacoes?: ILocacao[];
}

export class TipoEvento implements ITipoEvento {
    constructor(public id?: number, public descricao?: string, public locacoes?: ILocacao[]) {}
}
