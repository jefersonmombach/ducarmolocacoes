import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';
import { Principal } from 'app/core';
import { LocacaoProdutoService } from './locacao-produto.service';

@Component({
    selector: 'jhi-locacao-produto',
    templateUrl: './locacao-produto.component.html'
})
export class LocacaoProdutoComponent implements OnInit, OnDestroy {
    locacaoProdutos: ILocacaoProduto[];
    currentAccount: any;
    eventSubscriber: Subscription;
    currentSearch: string;

    constructor(
        private locacaoProdutoService: LocacaoProdutoService,
        private jhiAlertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
    ) {
        this.currentSearch =
            this.activatedRoute.snapshot && this.activatedRoute.snapshot.params['search']
                ? this.activatedRoute.snapshot.params['search']
                : '';
    }

    loadAll() {
        if (this.currentSearch) {
            this.locacaoProdutoService
                .search({
                    query: this.currentSearch
                })
                .subscribe(
                    (res: HttpResponse<ILocacaoProduto[]>) => (this.locacaoProdutos = res.body),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
            return;
        }
        this.locacaoProdutoService.query().subscribe(
            (res: HttpResponse<ILocacaoProduto[]>) => {
                this.locacaoProdutos = res.body;
                this.currentSearch = '';
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    search(query) {
        if (!query) {
            return this.clear();
        }
        this.currentSearch = query;
        this.loadAll();
    }

    clear() {
        this.currentSearch = '';
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.principal.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInLocacaoProdutos();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ILocacaoProduto) {
        return item.id;
    }

    registerChangeInLocacaoProdutos() {
        this.eventSubscriber = this.eventManager.subscribe('locacaoProdutoListModification', response => this.loadAll());
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
