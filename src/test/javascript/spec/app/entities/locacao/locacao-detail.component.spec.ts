/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { LocacaoDetailComponent } from 'app/entities/locacao/locacao-detail.component';
import { Locacao } from 'app/shared/model/locacao.model';

describe('Component Tests', () => {
    describe('Locacao Management Detail Component', () => {
        let comp: LocacaoDetailComponent;
        let fixture: ComponentFixture<LocacaoDetailComponent>;
        const route = ({ data: of({ locacao: new Locacao(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [LocacaoDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(LocacaoDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(LocacaoDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.locacao).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
