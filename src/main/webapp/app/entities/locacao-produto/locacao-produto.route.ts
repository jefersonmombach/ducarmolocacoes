import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { LocacaoProduto } from 'app/shared/model/locacao-produto.model';
import { LocacaoProdutoService } from './locacao-produto.service';
import { LocacaoProdutoComponent } from './locacao-produto.component';
import { LocacaoProdutoDetailComponent } from './locacao-produto-detail.component';
import { LocacaoProdutoUpdateComponent } from './locacao-produto-update.component';
import { LocacaoProdutoDeletePopupComponent } from './locacao-produto-delete-dialog.component';
import { ILocacaoProduto } from 'app/shared/model/locacao-produto.model';

@Injectable({ providedIn: 'root' })
export class LocacaoProdutoResolve implements Resolve<ILocacaoProduto> {
    constructor(private service: LocacaoProdutoService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((locacaoProduto: HttpResponse<LocacaoProduto>) => locacaoProduto.body));
        }
        return of(new LocacaoProduto());
    }
}

export const locacaoProdutoRoute: Routes = [
    {
        path: 'locacao-produto',
        component: LocacaoProdutoComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.locacaoProduto.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'locacao-produto/:id/view',
        component: LocacaoProdutoDetailComponent,
        resolve: {
            locacaoProduto: LocacaoProdutoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.locacaoProduto.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'locacao-produto/new',
        component: LocacaoProdutoUpdateComponent,
        resolve: {
            locacaoProduto: LocacaoProdutoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.locacaoProduto.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'locacao-produto/:id/edit',
        component: LocacaoProdutoUpdateComponent,
        resolve: {
            locacaoProduto: LocacaoProdutoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.locacaoProduto.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const locacaoProdutoPopupRoute: Routes = [
    {
        path: 'locacao-produto/:id/delete',
        component: LocacaoProdutoDeletePopupComponent,
        resolve: {
            locacaoProduto: LocacaoProdutoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.locacaoProduto.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
