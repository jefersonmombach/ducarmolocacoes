import { AfterViewInit, Component, HostBinding, Input, OnInit } from '@angular/core';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { ILocacaoProduto, LocacaoProduto } from 'app/shared/model/locacao-produto.model';
import { IProduto } from 'app/shared/model/produto.model';
import { ProdutoService } from 'app/entities/produto';
import { ILocacao } from 'app/shared/model/locacao.model';
import { LocacaoService } from 'app/entities/locacao';
import { ICliente } from 'app/shared/model/cliente.model';
import { ClienteService } from 'app/entities/cliente';
import { LocacaoProdutoService } from 'app/entities/locacao-produto';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
    selector: 'jhi-locacao-add-produto',
    templateUrl: './locacao-add-produto.component.html'
})
export class LocacaoAddProdutoComponent implements AfterViewInit {
    locacaoProduto: ILocacaoProduto = new LocacaoProduto();

    isSaving: boolean;

    produtos: IProduto[];
    locacaos: ILocacao[];
    clientes: ICliente[];

    @Input() locacao: ILocacao;

    constructor(
        private jhiAlertService: JhiAlertService,
        private locacaoProdutoService: LocacaoProdutoService,
        private produtoService: ProdutoService,
        private locacaoService: LocacaoService,
        private clienteService: ClienteService,
        private activeModal: NgbActiveModal
    ) {}

    ngAfterViewInit() {
        this.isSaving = false;
        this.locacaoProduto.locacaoId = this.locacao.id;
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
        this.close(null);
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

    trackClienteById(index: number, item: ICliente) {
        return item.id;
    }

    close(event) {
        this.activeModal.dismiss('');
    }

    calcularTotal() {
        this.locacaoProduto.valorTotal = this.locacaoProduto.valorUnitario * this.locacaoProduto.quantidade;
    }

    fireProduto(event) {
        this.locacaoProduto.produtoId = this.locacaoProduto.produto.id;
        this.locacaoProduto.quantidade = 1;
        this.locacaoProduto.valorUnitario = this.locacaoProduto.produto.precoVenda;
    }

    fireCliente($event) {
        this.locacaoProduto.clienteId = this.locacaoProduto.cliente.id;
    }

    addProduto(event) {
        this.calcularTotal();
        this.locacaoProduto.clienteId = this.locacaoProduto.cliente.id;
        this.locacaoProduto.produtoId = this.locacaoProduto.produto.id;
        this.locacaoProduto.locacaoId = this.locacao.id;
        this.activeModal.close(this.locacaoProduto);
    }
}
