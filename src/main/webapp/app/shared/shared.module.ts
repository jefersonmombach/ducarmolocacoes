import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbDateAdapter, NgbDateParserFormatter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter, NgbDateCustomParserFormatter } from './util/datepicker-adapter';
import { DucarmolocacoesSharedLibsModule, DucarmolocacoesSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
    imports: [DucarmolocacoesSharedLibsModule, DucarmolocacoesSharedCommonModule],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
    providers: [
        { provide: NgbDateAdapter, useClass: NgbDateMomentAdapter },
        { provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter }
    ],
    entryComponents: [JhiLoginModalComponent],
    exports: [DucarmolocacoesSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesSharedModule {}
