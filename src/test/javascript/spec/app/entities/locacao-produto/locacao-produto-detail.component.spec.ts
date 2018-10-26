/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoProdutoDetailComponent } from 'app/entities/locacao-produto/locacao-produto-detail.component';
import { LocacaoProduto } from 'app/shared/model/locacao-produto.model';

describe('Component Tests', () => {
    describe('LocacaoProduto Management Detail Component', () => {
        let comp: LocacaoProdutoDetailComponent;
        let fixture: ComponentFixture<LocacaoProdutoDetailComponent>;
        const route = ({ data: of({ locacaoProduto: new LocacaoProduto(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoProdutoDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(LocacaoProdutoDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LocacaoProdutoDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.locacaoProduto).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
