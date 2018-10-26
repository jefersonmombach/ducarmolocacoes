import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { of } from 'rxjs';
import { map } from 'rxjs/operators';
import { TipoEvento } from 'app/shared/model/tipo-evento.model';
import { TipoEventoService } from './tipo-evento.service';
import { TipoEventoComponent } from './tipo-evento.component';
import { TipoEventoDetailComponent } from './tipo-evento-detail.component';
import { TipoEventoUpdateComponent } from './tipo-evento-update.component';
import { TipoEventoDeletePopupComponent } from './tipo-evento-delete-dialog.component';
import { ITipoEvento } from 'app/shared/model/tipo-evento.model';

@Injectable({ providedIn: 'root' })
export class TipoEventoResolve implements Resolve<ITipoEvento> {
    constructor(private service: TipoEventoService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(map((tipoEvento: HttpResponse<TipoEvento>) => tipoEvento.body));
        }
        return of(new TipoEvento());
    }
}

export const tipoEventoRoute: Routes = [
    {
        path: 'tipo-evento',
        component: TipoEventoComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.tipoEvento.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tipo-evento/:id/view',
        component: TipoEventoDetailComponent,
        resolve: {
            tipoEvento: TipoEventoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.tipoEvento.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tipo-evento/new',
        component: TipoEventoUpdateComponent,
        resolve: {
            tipoEvento: TipoEventoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.tipoEvento.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'tipo-evento/:id/edit',
        component: TipoEventoUpdateComponent,
        resolve: {
            tipoEvento: TipoEventoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.tipoEvento.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const tipoEventoPopupRoute: Routes = [
    {
        path: 'tipo-evento/:id/delete',
        component: TipoEventoDeletePopupComponent,
        resolve: {
            tipoEvento: TipoEventoResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'ducarmolocacoesApp.tipoEvento.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
