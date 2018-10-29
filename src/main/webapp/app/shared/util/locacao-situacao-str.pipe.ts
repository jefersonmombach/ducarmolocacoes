import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'locacaoSituacaoStrPipe' })
export class LocacaoSituacaoStrPipe implements PipeTransform {
    transform(value: number): string {
        let retorno: string;
        switch (value) {
            case 0:
                retorno = 'Nova';
                break;
            case 1:
                retorno = 'Contrato Emitido';
                break;
            case 2:
                retorno = 'Trajes Entregues';
                break;
            case 3:
                retorno = 'Trajes Devolvidos';
                break;
            case 9:
                retorno = 'Cancelado';
                break;
        }
        return retorno;
    }
}
