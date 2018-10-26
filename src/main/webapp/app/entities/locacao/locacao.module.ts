import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DucarmolocacoesSharedModule } from 'app/shared';
import {
    LocacaoComponent,
    LocacaoDetailComponent,
    LocacaoUpdateComponent,
    LocacaoDeletePopupComponent,
    LocacaoDeleteDialogComponent,
    locacaoRoute,
    locacaoPopupRoute
} from './';

const ENTITY_STATES = [...locacaoRoute, ...locacaoPopupRoute];

@NgModule({
    imports: [DucarmolocacoesSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        LocacaoComponent,
        LocacaoDetailComponent,
        LocacaoUpdateComponent,
        LocacaoDeleteDialogComponent,
        LocacaoDeletePopupComponent
    ],
    entryComponents: [LocacaoComponent, LocacaoUpdateComponent, LocacaoDeleteDialogComponent, LocacaoDeletePopupComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesLocacaoModule {}
