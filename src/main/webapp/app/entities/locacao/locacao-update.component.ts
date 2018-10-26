import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';
import { JhiAlertService } from 'ng-jhipster';

import { ILocacao } from 'app/shared/model/locacao.model';
import { LocacaoService } from './locacao.service';
import { ICliente } from 'app/shared/model/cliente.model';
import { ClienteService } from 'app/entities/cliente';
import { ITipoEvento } from 'app/shared/model/tipo-evento.model';
import { TipoEventoService } from 'app/entities/tipo-evento';

@Component({
    selector: 'jhi-locacao-update',
    templateUrl: './locacao-update.component.html'
})
export class LocacaoUpdateComponent implements OnInit {
    locacao: ILocacao;
    isSaving: boolean;

    clientes: ICliente[];

    tipoeventos: ITipoEvento[];
    dataContratacao: string;
    dataEventoDp: any;
    dataDevPrevDp: any;
    dataDevReal: string;
    dataEntrPrevDp: any;
    dataEntrReal: string;

    constructor(
        private jhiAlertService: JhiAlertService,
        private locacaoService: LocacaoService,
        private clienteService: ClienteService,
        private tipoEventoService: TipoEventoService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ locacao }) => {
            this.locacao = locacao;
            this.dataContratacao = this.locacao.dataContratacao != null ? this.locacao.dataContratacao.format(DATE_TIME_FORMAT) : null;
            this.dataDevReal = this.locacao.dataDevReal != null ? this.locacao.dataDevReal.format(DATE_TIME_FORMAT) : null;
            this.dataEntrReal = this.locacao.dataEntrReal != null ? this.locacao.dataEntrReal.format(DATE_TIME_FORMAT) : null;
        });
        this.clienteService.query().subscribe(
            (res: HttpResponse<ICliente[]>) => {
                this.clientes = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.tipoEventoService.query().subscribe(
            (res: HttpResponse<ITipoEvento[]>) => {
                this.tipoeventos = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        this.locacao.dataContratacao = this.dataContratacao != null ? moment(this.dataContratacao, DATE_TIME_FORMAT) : null;
        this.locacao.dataDevReal = this.dataDevReal != null ? moment(this.dataDevReal, DATE_TIME_FORMAT) : null;
        this.locacao.dataEntrReal = this.dataEntrReal != null ? moment(this.dataEntrReal, DATE_TIME_FORMAT) : null;
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
}
