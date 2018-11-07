import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JhiAlertService } from 'ng-jhipster';

import { ILocacao } from 'app/shared/model/locacao.model';
import { LocacaoService } from './locacao.service';
import { ICliente } from 'app/shared/model/cliente.model';
import { ClienteService } from 'app/entities/cliente';
import { ITipoEvento } from 'app/shared/model/tipo-evento.model';
import { TipoEventoService } from 'app/entities/tipo-evento';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { LocacaoAddProdutoComponent } from 'app/entities/locacao/locacao-add-produto.component';
import * as _ from 'lodash';

@Component({
    selector: 'jhi-locacao-update',
    templateUrl: './locacao-update.component.html'
})
export class LocacaoUpdateComponent implements OnInit {
    locacao: ILocacao;
    isSaving: boolean;

    clientes: ICliente[];
    tipoeventos: ITipoEvento[];

    dataEventoDp: any;
    dataDevPrevDp: any;
    dataEntrPrevDp: any;

    modalRef: NgbModalRef;

    constructor(
        private jhiAlertService: JhiAlertService,
        private locacaoService: LocacaoService,
        private clienteService: ClienteService,
        private tipoEventoService: TipoEventoService,
        private activatedRoute: ActivatedRoute,
        private modalService: NgbModal
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ locacao }) => {
            this.locacao = locacao;
        });
        this.loadClientes();
        this.loadTiposEvento();
        this.locacao.produtos = [];
    }

    private loadTiposEvento() {
        this.tipoEventoService.query().subscribe(
            (res: HttpResponse<ITipoEvento[]>) => {
                this.tipoeventos = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    private loadClientes() {
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
        this.calcularSaldo();
        if (this.locacao.id !== undefined) {
            this.subscribeToSaveResponse(this.locacaoService.update(this.locacao));
        } else {
            this.subscribeToSaveResponse(this.locacaoService.create(this.locacao));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ILocacao>>) {
        result.subscribe((res: HttpResponse<ILocacao>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackClienteById(index: number, item: ICliente) {
        return item.id;
    }

    trackTipoEventoById(index: number, item: ITipoEvento) {
        return item.id;
    }

    fireDataEvento(event: any) {
        if (event) {
            this.locacao.dataEntrPrev = event.clone().add(-1, 'days');
            this.locacao.dataDevPrev = event.clone().add(3, 'days');
        }
    }

    openAddProduto() {
        if (!_.isNil(this.locacao.dataEvento)) {
            const modalRef = this.modalService.open(LocacaoAddProdutoComponent);
            modalRef.componentInstance.locacao = this.locacao;
            modalRef.result.then(
                result => {
                    this.locacao.produtos.push(result);
                    this.calcular();
                },
                reason => {
                    console.log(reason);
                }
            );
        } else {
            alert('É necessário informar a data do evento.');
            return;
        }
    }

    removerProduto(locacaoProduto) {
        _.remove(this.locacao.produtos, e => e === locacaoProduto);
    }

    calcular() {
        let total = 0;
        this.locacao.produtos.forEach(item => (total += item.valorTotal));
        this.locacao.valorTotal = total;
        this.locacao.valorSinal = total * 20 / 100;
    }

    calcularSaldo() {
        this.locacao.valorSaldo = this.locacao.valorTotal - this.locacao.valorAdiantado;
    }
}
