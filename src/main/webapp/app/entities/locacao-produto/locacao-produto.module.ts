import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DucarmolocacoesSharedModule } from 'app/shared';
import {
    LocacaoProdutoComponent,
    LocacaoProdutoDetailComponent,
    LocacaoProdutoUpdateComponent,
    LocacaoProdutoDeletePopupComponent,
    LocacaoProdutoDeleteDialogComponent,
    locacaoProdutoRoute,
    locacaoProdutoPopupRoute
} from './';

const ENTITY_STATES = [...locacaoProdutoRoute, ...locacaoProdutoPopupRoute];

@NgModule({
    imports: [DucarmolocacoesSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        LocacaoProdutoComponent,
        LocacaoProdutoDetailComponent,
        LocacaoProdutoUpdateComponent,
        LocacaoProdutoDeleteDialogComponent,
        LocacaoProdutoDeletePopupComponent
    ],
    entryComponents: [
        LocacaoProdutoComponent,
        LocacaoProdutoUpdateComponent,
        LocacaoProdutoDeleteDialogComponent,
        LocacaoProdutoDeletePopupComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesLocacaoProdutoModule {}
