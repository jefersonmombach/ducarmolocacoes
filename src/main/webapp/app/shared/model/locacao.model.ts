import { Moment } from 'moment';
import { ILocacaoProduto } from 'app/shared/model//locacao-produto.model';

export interface ILocacao {
    id?: number;
    valorTotal?: number;
    valorSinal?: number;
    valorAdiantado?: number;
    valorSaldo?: number;
    dataContratacao?: Moment;
    dataEvento?: Moment;
    dataDevPrev?: Moment;
    dataDevReal?: Moment;
    dataEntrPrev?: Moment;
    dataEntrReal?: Moment;
    situacao?: number;
    htmlContrato?: string;
    produtos?: ILocacaoProduto[];
    clienteId?: number;
    tipoEventoId?: number;
}

export class Locacao implements ILocacao {
    constructor(
        public id?: number,
        public valorTotal?: number,
        public valorSinal?: number,
        public valorAdiantado?: number,
        public valorSaldo?: number,
        public dataContratacao?: Moment,
        public dataEvento?: Moment,
        public dataDevPrev?: Moment,
        public dataDevReal?: Moment,
        public dataEntrPrev?: Moment,
        public dataEntrReal?: Moment,
        public situacao?: number,
        public htmlContrato?: string,
        public produtos?: ILocacaoProduto[],
        public clienteId?: number,
        public tipoEventoId?: number
    ) {}
}
