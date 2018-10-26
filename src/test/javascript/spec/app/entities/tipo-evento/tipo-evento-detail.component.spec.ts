/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DucarmolocacoesTestModule } from '../../../test.module';
import { TipoEventoDetailComponent } from 'app/entities/tipo-evento/tipo-evento-detail.component';
import { TipoEvento } from 'app/shared/model/tipo-evento.model';

describe('Component Tests', () => {
    describe('TipoEvento Management Detail Component', () => {
        let comp: TipoEventoDetailComponent;
        let fixture: ComponentFixture<TipoEventoDetailComponent>;
        const route = ({ data: of({ tipoEvento: new TipoEvento(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [DucarmolocacoesTestModule],
                declarations: [TipoEventoDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(TipoEventoDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(TipoEventoDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.tipoEvento).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
