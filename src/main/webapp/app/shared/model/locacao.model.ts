import { Moment } from 'moment';
import * as moment from 'moment';
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
    ) {
        this.dataContratacao = this.dataContratacao || moment();
        this.situacao = this.situacao || 0;
        this.valorTotal = this.valorTotal || 0;
        this.valorSinal = this.valorSinal || 0;
        this.valorAdiantado = this.valorAdiantado || 0;
        this.valorSaldo = this.valorSaldo || 0;
    }
}
