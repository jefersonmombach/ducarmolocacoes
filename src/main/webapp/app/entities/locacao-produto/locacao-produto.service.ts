import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';

type EntityResponseType = HttpResponse<ILocacaoProduto>;
type EntityArrayResponseType = HttpResponse<ILocacaoProduto[]>;

@Injectable({ providedIn: 'root' })
export class LocacaoProdutoService {
    public resourceUrl = SERVER_API_URL + 'api/locacao-produtos';
    public resourceSearchUrl = SERVER_API_URL + 'api/_search/locacao-produtos';

    constructor(private http: HttpClient) {}

    create(locacaoProduto: ILocacaoProduto): Observable<EntityResponseType> {
        return this.http.post<ILocacaoProduto>(this.resourceUrl, locacaoProduto, { observe: 'response' });
    }

    update(locacaoProduto: ILocacaoProduto): Observable<EntityResponseType> {
        return this.http.put<ILocacaoProduto>(this.resourceUrl, locacaoProduto, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ILocacaoProduto>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILocacaoProduto[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    search(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ILocacaoProduto[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
    }
}
