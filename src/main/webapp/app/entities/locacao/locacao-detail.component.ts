import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILocacao } from 'app/shared/model/locacao.model';

@Component({
    selector: 'jhi-locacao-detail',
    templateUrl: './locacao-detail.component.html'
})
export class LocacaoDetailComponent implements OnInit {
    locacao: ILocacao;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ locacao }) => {
            this.locacao = locacao;
        });
    }

    previousState() {
        window.history.back();
    }
}
