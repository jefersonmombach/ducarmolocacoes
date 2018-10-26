import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';
import { LocacaoProdutoService } from './locacao-produto.service';
import { IProduto } from 'app/shared/model/produto.model';
import { ProdutoService } from 'app/entities/produto';
import { ILocacao } from 'app/shared/model/locacao.model';
import { LocacaoService } from 'app/entities/locacao';
import { ICliente } from 'app/shared/model/cliente.model';
import { ClienteService } from 'app/entities/cliente';

@Component({
    selector: 'jhi-locacao-produto-update',
    templateUrl: './locacao-produto-update.component.html'
})
export class LocacaoProdutoUpdateComponent implements OnInit {
    locacaoProduto: ILocacaoProduto;
    isSaving: boolean;

    produtos: IProduto[];

    locacaos: ILocacao[];

    clientes: ICliente[];

    constructor(
        private jhiAlertService: JhiAlertService,
        private locacaoProdutoService: LocacaoProdutoService,
        private produtoService: ProdutoService,
        private locacaoService: LocacaoService,
        private clienteService: ClienteService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ locacaoProduto }) => {
            this.locacaoProduto = locacaoProduto;
        });
        this.produtoService.query().subscribe(
            (res: HttpResponse<IProduto[]>) => {
                this.produtos = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.locacaoService.query().subscribe(
            (res: HttpResponse<ILocacao[]>) => {
                this.locacaos = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.clienteService.query().subscribe(
            (res: HttpResponse<ICliente[]>) => {
                this.clientes = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.locacaoProduto.id !== undefined) {
            this.subscribeToSaveResponse(this.locacaoProdutoService.update(this.locacaoProduto));
        } else {
            this.subscribeToSaveResponse(this.locacaoProdutoService.create(this.locacaoProduto));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ILocacaoProduto>>) {
        result.subscribe((res: HttpResponse<ILocacaoProduto>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackProdutoById(index: number, item: IProduto) {
        return item.id;
    }

    trackLocacaoById(index: number, item: ILocacao) {
        return item.id;
    }

    trackClienteById(index: number, item: ICliente) {
        return item.id;
    }
}
