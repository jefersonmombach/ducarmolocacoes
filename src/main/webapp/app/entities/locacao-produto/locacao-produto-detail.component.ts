import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';

@Component({
    selector: 'jhi-locacao-produto-detail',
    templateUrl: './locacao-produto-detail.component.html'
})
export class LocacaoProdutoDetailComponent implements OnInit {
    locacaoProduto: ILocacaoProduto;

    constructor(private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ locacaoProduto }) => {
            this.locacaoProduto = locacaoProduto;
        });
    }

    previousState() {
        window.history.back();
    }
}
