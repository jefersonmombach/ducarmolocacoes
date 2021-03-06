import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption, DATE_FORMAT } from 'app/shared';
import { IProduto } from 'app/shared/model/produto.model';
import { ILocacao } from 'app/shared/model/locacao.model';

type EntityResponseType = HttpResponse<IProduto>;
type EntityArrayResponseType = HttpResponse<IProduto[]>;

@Injectable({ providedIn: 'root' })
export class ProdutoService {
    public resourceUrl = SERVER_API_URL + 'api/produtos';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/produtos';

    constructor(private http: HttpClient) {}

    create(produto: IProduto): Observable<EntityResponseType> {
        return this.http.post<IProduto>(this.resourceUrl, produto, { observe: 'response' });
    }

    update(produto: IProduto): Observable<EntityResponseType> {
        return this.http.put<IProduto>(this.resourceUrl, produto, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IProduto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    getLocacoesByDataEvento(id: number, dataEvento: any): Observable<EntityArrayResponseType> {
        return this.http.get<ILocacao[]>(
            `${this.resourceUrl}/${id}/locacaos/${dataEvento != null && dataEvento.isValid() ? dataEvento.format(DATE_FORMAT) : null}`,
            { observe: 'response' }
        );
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IProduto[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IProduto[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
