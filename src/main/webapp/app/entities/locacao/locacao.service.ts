import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ILocacao } from 'app/shared/model/locacao.model';

type EntityResponseType = HttpResponse<ILocacao>;
type EntityArrayResponseType = HttpResponse<ILocacao[]>;

@Injectable({ providedIn: 'root' })
export class LocacaoService {
    public resourceUrl = SERVER_API_URL + 'api/locacaos';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/locacaos';

    constructor(private http: HttpClient) {}

    create(locacao: ILocacao): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(locacao);
        return this.http
            .post<ILocacao>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    update(locacao: ILocacao): Observable<EntityResponseType> {
        const copy = this.convertDateFromClient(locacao);
        return this.http
            .put<ILocacao>(this.resourceUrl, copy, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http
            .get<ILocacao>(`${this.resourceUrl}/${id}`, { observe: 'response' })
            .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ILocacao[]>(this.resourceUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http
            .get<ILocacao[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
            .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
    }

    private convertDateFromClient(locacao: ILocacao): ILocacao {
        const copy: ILocacao = Object.assign({}, locacao, {
            dataContratacao: locacao.dataContratacao != null && locacao.dataContratacao.isValid() ? locacao.dataContratacao.toJSON() : null,
            dataEvento: locacao.dataEvento != null && locacao.dataEvento.isValid() ? locacao.dataEvento.format(DATE_FORMAT) : null,
            dataDevPrev: locacao.dataDevPrev != null && locacao.dataDevPrev.isValid() ? locacao.dataDevPrev.format(DATE_FORMAT) : null,
            dataDevReal: locacao.dataDevReal != null && locacao.dataDevReal.isValid() ? locacao.dataDevReal.toJSON() : null,
            dataEntrPrev: locacao.dataEntrPrev != null && locacao.dataEntrPrev.isValid() ? locacao.dataEntrPrev.format(DATE_FORMAT) : null,
            dataEntrReal: locacao.dataEntrReal != null && locacao.dataEntrReal.isValid() ? locacao.dataEntrReal.toJSON() : null
        });
        return copy;
    }

    private convertDateFromServer(res: EntityResponseType): EntityResponseType {
        res.body.dataContratacao = res.body.dataContratacao != null ? moment(res.body.dataContratacao) : null;
        res.body.dataEvento = res.body.dataEvento != null ? moment(res.body.dataEvento) : null;
        res.body.dataDevPrev = res.body.dataDevPrev != null ? moment(res.body.dataDevPrev) : null;
        res.body.dataDevReal = res.body.dataDevReal != null ? moment(res.body.dataDevReal) : null;
        res.body.dataEntrPrev = res.body.dataEntrPrev != null ? moment(res.body.dataEntrPrev) : null;
        res.body.dataEntrReal = res.body.dataEntrReal != null ? moment(res.body.dataEntrReal) : null;
        return res;
    }

    private convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
        res.body.forEach((locacao: ILocacao) => {
            locacao.dataContratacao = locacao.dataContratacao != null ? moment(locacao.dataContratacao) : null;
            locacao.dataEvento = locacao.dataEvento != null ? moment(locacao.dataEvento) : null;
            locacao.dataDevPrev = locacao.dataDevPrev != null ? moment(locacao.dataDevPrev) : null;
            locacao.dataDevReal = locacao.dataDevReal != null ? moment(locacao.dataDevReal) : null;
            locacao.dataEntrPrev = locacao.dataEntrPrev != null ? moment(locacao.dataEntrPrev) : null;
            locacao.dataEntrReal = locacao.dataEntrReal != null ? moment(locacao.dataEntrReal) : null;
        });
        return res;
    }
}
