import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'booleanStr' })
export class BooleanStrPipe implements PipeTransform {
    transform(value: boolean): string {
        return value === true ? 'Sim' : 'Não';
    }
}
