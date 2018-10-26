/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoProdutoComponent } from 'app/entities/locacao-produto/locacao-produto.component';
import { LocacaoProdutoService } from 'app/entities/locacao-produto/locacao-produto.service';
import { LocacaoProduto } from 'app/shared/model/locacao-produto.model';

describe('Component Tests', () => {
    describe('LocacaoProduto Management Component', () => {
        let comp: LocacaoProdutoComponent;
        let fixture: ComponentFixture<LocacaoProdutoComponent>;
        let service: LocacaoProdutoService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoProdutoComponent],
                providers: []
            })
                .overrideTemplate(LocacaoProdutoComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(LocacaoProdutoComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(LocacaoProdutoService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new LocacaoProduto(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.locacaoProdutos[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
