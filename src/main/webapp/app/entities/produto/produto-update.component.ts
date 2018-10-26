import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { IProduto } from 'app/shared/model/produto.model';
import { ProdutoService } from './produto.service';

@Component({
    selector: 'jhi-produto-update',
    templateUrl: './produto-update.component.html'
})
export class ProdutoUpdateComponent implements OnInit {
    produto: IProduto;
    isSaving: boolean;

    constructor(private produtoService: ProdutoService, private activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ produto }) => {
            this.produto = produto;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.produto.id !== undefined) {
            this.subscribeToSaveResponse(this.produtoService.update(this.produto));
        } else {
            this.subscribeToSaveResponse(this.produtoService.create(this.produto));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<IProduto>>) {
        result.subscribe((res: HttpResponse<IProduto>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }
}
