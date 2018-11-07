import { Directive, ElementRef, HostListener, Output, EventEmitter, Input, OnInit, DoCheck } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Diretiva para converter um campo de texto para
 * valor numerico no model correspondente
 */
@Directive({
    // tslint:disable-next-line:directive-selector
    selector: '[sng-numeric]'
})
export class SngNumericDirective implements OnInit, DoCheck {
    @Output() ngModelChange: EventEmitter<any> = new EventEmitter<any>(false);

    // tslint:disable-next-line:no-input-rename
    @Input('sng-numeric-cfg')
    config = {
        decimalSymb: ','
    };

    private regex: RegExp;

    constructor(private elementRef: ElementRef, private model: NgControl) {
        this.elementRef.nativeElement.style.textAlign = 'right';
    }

    ngOnInit() {
        if (this.config.decimalSymb === '.') {
            this.config.decimalSymb = '\\.';
        }
        this.regex = new RegExp(`[^\\d-${this.config.decimalSymb}]`, 'g');
    }

    ngDoCheck() {
        const elVal = this.elementRef.nativeElement.value;
        const modelVal = this.model.control.value;

        // quando os valores sao iguais (inicializacao de valores a posteriori)
        if (modelVal && elVal === modelVal) {
            // console.log(`MODEL=${this.model.value} --- INPUT=${elVal}`);
            this.unmaskInputToModel(); // aplica o unmask e atribui o valor numerico
        }
    }

    @HostListener('input')
    inputChange() {
        this.unmaskInputToModel();
    }

    private unmaskInputToModel() {
        const newValue = this.unMask();
        this.model.control.setValue(newValue, {
            emitEvent: false,
            emitModelToViewChange: false,
            emitViewToModelChange: false
        });
    }

    @HostListener('focusout')
    onFocusout() {
        this.ngModelChange.emit(this.unMask());
    }

    @HostListener('focus')
    onFocus() {
        if (this.model.control.value === 0) {
            this.elementRef.nativeElement.setSelectionRange(0, this.elementRef.nativeElement.value.length);
        } else {
            this.elementRef.nativeElement.setSelectionRange(
                this.elementRef.nativeElement.value.length - 3,
                this.elementRef.nativeElement.value.length - 3
            );
        }
    }

    unMask() {
        let val: string = this.elementRef.nativeElement.value.replace(this.regex, '', '');
        if (this.config.decimalSymb === ',') {
            val = val.replace(/,/, '.');
        }
        if (val.substring(val.indexOf('.') + 1) === '') {
            val += '0';
        }
        return Number(val);
    }
}
