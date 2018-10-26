import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { DucarmolocacoesTipoEventoModule } from './tipo-evento/tipo-evento.module';
import { DucarmolocacoesProdutoModule } from './produto/produto.module';
import { DucarmolocacoesClienteModule } from './cliente/cliente.module';
import { DucarmolocacoesLocacaoModule } from './locacao/locacao.module';
import { DucarmolocacoesLocacaoProdutoModule } from './locacao-produto/locacao-produto.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        DucarmolocacoesTipoEventoModule,
        DucarmolocacoesProdutoModule,
        DucarmolocacoesClienteModule,
        DucarmolocacoesLocacaoModule,
        DucarmolocacoesLocacaoProdutoModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DucarmolocacoesEntityModule {}
