import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DucarmolocacoesSharedModule } from 'app/shared';
import {
    ClienteComponent,
    ClienteDetailComponent,
    ClienteUpdateComponent,
    ClienteDeletePopupComponent,
    ClienteDeleteDialogComponent,
    clienteRoute,
    clientePopupRoute
} from './';

import { NgxMaskModule } from 'ngx-mask';

const ENTITY_STATES = [...clienteRoute, ...clientePopupRoute];

@NgModule({
    imports: [DucarmolocacoesSharedModule, RouterModule.forChild(ENTITY_STATES), NgxMaskModule.forRoot({})],
    declarations: [
        ClienteComponent,
        ClienteDetailComponent,
        ClienteUpdateComponent,
        ClienteDeleteDialogComponent,
        ClienteDeletePopupComponent
    ],
    entryComponents: [ClienteComponent, ClienteUpdateComponent, ClienteDeleteDialogComponent, ClienteDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesClienteModule {}
