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

import { CurrencyMaskModule } from 'ng2-currency-mask';
import {
    LocacaoMudarSituacaoDialogComponent,
    LocacaoMudarSituacaoPopupComponent
} from 'app/entities/locacao/locacao-mudar-situacao-dialog.component';
import { LocacaoSituacaoStrPipe } from 'app/shared/util/locacao-situacao-str.pipe';
import { LocacaoAddProdutoComponent } from 'app/entities/locacao/locacao-add-produto.component';

const ENTITY_STATES = [...locacaoRoute, ...locacaoPopupRoute];

@NgModule({
    imports: [DucarmolocacoesSharedModule, CurrencyMaskModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        LocacaoComponent,
        LocacaoDetailComponent,
        LocacaoUpdateComponent,
        LocacaoDeleteDialogComponent,
        LocacaoDeletePopupComponent,
        LocacaoMudarSituacaoDialogComponent,
        LocacaoMudarSituacaoPopupComponent,
        LocacaoSituacaoStrPipe,
        LocacaoAddProdutoComponent
    ],
    entryComponents: [
        LocacaoComponent,
        LocacaoUpdateComponent,
        LocacaoDeleteDialogComponent,
        LocacaoDeletePopupComponent,
        LocacaoMudarSituacaoDialogComponent,
        LocacaoMudarSituacaoPopupComponent,
        LocacaoAddProdutoComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesLocacaoModule {}
